package encrypted.dssb.gamemode;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.util.TeleportHelper;
import encrypted.dssb.util.WorldHelper;
import net.minecraft.block.Material;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GameMode {
    public boolean DirtyCard = false;
    public MinecraftServer Server;

    public BingoCard Card;

    public HashMap<AbstractTeam, BlockPos> TeamSpawns = new HashMap<>();

    public GameStatus Status = GameStatus.Idle;
    public long CountDownStart;
    public long CurrentCountdownSecond;

    public boolean TimerRunning = false;
    public long TimerStart;
    public long CurrentTimerSecond;

    public GameMode(MinecraftServer server) {
        Server = server;
    }

    public abstract void start();

    public abstract void end();

    public abstract void handleCountdown();

    public abstract void handleTimer();

    public abstract boolean checkItem(Item item, PlayerEntity player);

    public abstract boolean checkBingo(AbstractTeam team);

    public void initialize() {
        var world = WorldHelper.getWorldByName(Server, BingoManager.GameSettings.Dimension);

        if (world == null) {
            BingoMod.LOGGER.error("Unable to initialize game. World is null.");
            return;
        }

        if (DirtyCard) Card.resetCard(world);
        DirtyCard = true;

        var origin = getRandomPos(
                world,
                -BingoManager.GameSettings.PlayAreaRadius + BingoManager.GameSettings.TPRandomizationRadius,
                BingoManager.GameSettings.PlayAreaRadius - BingoManager.GameSettings.TPRandomizationRadius,
                -BingoManager.GameSettings.PlayAreaRadius + BingoManager.GameSettings.TPRandomizationRadius,
                BingoManager.GameSettings.PlayAreaRadius - BingoManager.GameSettings.TPRandomizationRadius
        );

        TeamSpawns = findTeamSpawns(
                world,
                new Vec2f(origin.getX(), origin.getZ()),
                100,
                BingoManager.GameSettings.TPRandomizationRadius,
                BingoManager.GameSettings.MaxYLevel);
        for (var spawn : TeamSpawns.values())
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos(spawn), 1, 0);

        for (var player : BingoManager.getValidPlayers(Server.getPlayerManager())) {
            player.getInventory().clear();
            player.getInventory().offHand.set(0, Card.getMap());
            player.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        }

        Status = GameStatus.Initializing;
    }

    public void playNotificationSound(World world) {
        for (var player : world.getPlayers())
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);
    }

    public void teleportPlayersToTeamSpawns(ServerWorld world) {
        for (var team : TeamSpawns.entrySet()) {
            if (team.getKey() == null || team.getKey().getPlayerList().size() == 0)
                continue;

            for (var name : team.getKey().getPlayerList()) {
                var player = Server.getPlayerManager().getPlayer(name);
                if (player != null) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300 * 20, 255, false, false, false));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 300 * 20, 255, false, false, false));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 300 * 20, 250, false, false, false));
                    teleportPlayerToTeamSpawn(world, player, team.getValue().offset(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset));
                }
            }
        }
    }

    public void teleportPlayerToTeamSpawn(ServerWorld world, ServerPlayerEntity player, BlockPos spawn) {
        if (player != null && BingoManager.BingoPlayers.contains(player.getUuid())) {
            try {
                player.setMovementSpeed(0);
                player.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
                TeleportHelper.teleport(player, world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0, 0);
                player.setSpawnPoint(player.getWorld().getRegistryKey(), spawn, 0, true, false);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public BlockPos getRandomPos(ServerWorld world, int xMin, int xMax, int zMin, int zMax) {
        while (true) {
            var x = ThreadLocalRandom.current().nextInt(xMin, xMax + 1);
            var z = ThreadLocalRandom.current().nextInt(zMin, zMax + 1);
            var pos = new BlockPos(x - 0.5, 200, z - 0.5);
            var biome = world.getBiome(pos);

            if (biome.getKey().isPresent() && biome.getKey().get() != BiomeKeys.OCEAN && biome.getKey().get() != BiomeKeys.BEACH)
                return pos;
        }
    }

    public HashMap<AbstractTeam, BlockPos> findTeamSpawns(ServerWorld world, Vec2f center, float spreadDistance, float maxRange, int maxY) {
        var spawns = new HashMap<AbstractTeam, BlockPos>();
        var random = Random.create();

        for (var team : world.getScoreboard().getTeams()) {
            var attempts = 0;
            var found = false;
            while (!found && attempts < 100) {
                attempts++;
                var x = Math.floor(MathHelper.nextDouble(random, center.x - maxRange, center.x + maxRange));
                var z = Math.floor(MathHelper.nextDouble(random, center.y - maxRange, center.y + maxRange));

                // check if point is withing spread distance to another spawn
                var tooClose = spawns.values().stream().anyMatch(spawn -> {
                    var distance = Math.sqrt(Math.pow(x - spawn.getX(), 2) + Math.pow(z - spawn.getZ(), 2));
                    return distance < spreadDistance;
                });
                if (tooClose) continue;

                // get the top block
                var mutable = new BlockPos.Mutable(x, maxY + 1, z);
                var headValid = world.getBlockState(mutable).isAir();
                mutable.move(Direction.DOWN);
                var footValid = world.getBlockState(mutable).isAir();
                if (!headValid || !footValid) continue;

                while (world.getBlockState(mutable).isAir() && mutable.getY() > world.getBottomY())
                    mutable.move(Direction.DOWN);

                // check if top block is valid
                var material = world.getBlockState(mutable).getMaterial();
                if (material.isLiquid() || material == Material.FIRE) continue;

                mutable.move(Direction.UP);
                spawns.put(team, mutable);
                found = true;
            }
        }
        return spawns;
    }
}
