package encrypted.dssb.gamemode;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.model.BingoItem;
import encrypted.dssb.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GameModeBase {
    public boolean DirtyCard = false;
    public MinecraftServer Server;

    protected BingoCard Card;

    public HashMap<Team, BlockPos> TeamSpawns = new HashMap<>();

    public GameStatus Status = GameStatus.Idle;
    public long CountDownStart;
    public long CurrentCountdownSecond;

    public boolean TimerRunning = false;
    public long TimerStart;
    public long CurrentTimerSecond;

    public String Name = "Base";

    public GameModeBase(MinecraftServer server) { Server = server; }

    public abstract void start();

    public abstract void end();

    public abstract void handleCountdown();

    public abstract void handleTimer();

    public abstract boolean checkBingo(Team team);

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

        CompletableFuture.runAsync(() -> {
            for (var team : world.getScoreboard().getPlayerTeams()) {
                var pos = findTeamSpawn(
                        TeamSpawns,
                        world,
                        new Vec2(origin.getX(), origin.getZ()),
                        100,
                        BingoManager.GameSettings.TPRandomizationRadius,
                        BingoManager.GameSettings.MaxYLevel);
                if (pos != null)
                    TeamSpawns.put(team, pos);
                else {
                    MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), TranslationHelper.getAsText("dssb.error.unable_to_find_spawn", team.getName()));
                    end();
                }
            }

            for (var player : BingoManager.getValidPlayers(Server.getPlayerList())) {
                player.getInventory().clearContent();
                player.getInventory().setItem(Inventory.SLOT_OFFHAND, Card.getMap());
                player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
            }
            Status = GameStatus.Initializing;
        }).exceptionally(e -> {
            MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), TranslationHelper.getAsText("dssb.error.spawn_problem"));
            BingoMod.LOGGER.error(e.getMessage());
            return null;
        });
    }

    public boolean checkItem(Item item, Player player) {
        var foundByTeam = player.getTeam();
        var server = player.level().getServer();
        if (foundByTeam == null || server == null)
            return false;

        var rowIndex = 0;
        for (var row : Card.slots) {
            var colIndex = 0;
            for (var bingoItem : row) {
                if (bingoItem.item == item) {
                    for (var team : bingoItem.teams) {
                        if (team == foundByTeam)
                            return false;
                    }

                    bingoItem.teams.add(foundByTeam);
                    Card.updateMap(player, rowIndex, colIndex, false);

                    final Component itemFound = TranslationHelper.getAsText("dssb.game.item_found", PlayerHelper.getPlayerName(player), item.getName().getString()).withStyle(foundByTeam.getColor());
                    MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), itemFound);
                    playNotificationSound(player.level());
                    return true;
                }
                colIndex++;
            }
            rowIndex++;
        }
        return false;
    }

    public void handleWin(Team team) {
        TimerRunning = false;

        var timeDif = System.currentTimeMillis() - TimerStart;
        var millis = timeDif % 1000;
        var second = (timeDif / 1000) % 60;
        var minute = (timeDif / (1000 * 60)) % 60;
        var hour = (timeDif / (1000 * 60 * 60)) % 24;
        var readableTime = "";
        if (hour > 0) readableTime = String.format("%d:%02d:%02d.%d", hour, minute, second, millis);
        else readableTime = String.format("%d:%02d.%d", minute, second, millis);

        final Component bingoFinished = TranslationHelper.getAsText("dssb.game.team_wins",team.getName(), readableTime).withStyle(team.getColor());
        MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), bingoFinished);

        var world = WorldHelper.getWorldByName(Server, BingoMod.CONFIG.SpawnSettings.Dimension);
        if (world != null) {
            for (var i = 0; i < Card.size; i++) {
                for (var j = 0; j < Card.size; j++) {
                    var slot = Card.slots[i][j];
                    var framePos = BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().relative(Direction.Axis.Y, Card.size - 1 - i).relative(Direction.EAST, j);
                    if (slot.teams.contains(team))
                        world.setBlockAndUpdate(framePos, getConcrete(team));
                }
            }
        }
        setScoreboardStats(getTeamNumber(team));

        Status = GameStatus.Idle;
    }

    public void playNotificationSound(Level world) {
        for (var player : world.players())
            player.level().playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 1, 1);
    }

    public void teleportPlayersToTeamSpawns(ServerLevel world) {
        for (var team : TeamSpawns.entrySet()) {
            if (team.getKey() == null || team.getKey().getPlayers().isEmpty())
                continue;

            for (var name : team.getKey().getPlayers()) {
                var player = Server.getPlayerList().getPlayerByName(name);
                if (player != null) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300 * 20, 255, false, false, false));
                    player.setNoGravity(true);
                    teleportPlayerToTeamSpawn(world, player, team.getValue().relative(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset));
                }
            }
        }
    }

    public void teleportPlayerToTeamSpawn(ServerLevel world, ServerPlayer player, BlockPos spawn) {
        if (player != null && BingoManager.BingoPlayers.contains(player.getUUID())) {
            try {
                player.setSpeed(0);
                player.getFoodData().setFoodLevel(20);
                TeleportHelper.teleport(player, world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0, 0);
                player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
                player.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(player.level().dimension(), spawn, 0.0f, 0.0f), true), false);
            } catch (CommandSyntaxException e) {
                BingoMod.LOGGER.error(e.getMessage());
            }
        }
    }

    public BlockPos getRandomPos(ServerLevel world, int xMin, int xMax, int zMin, int zMax) {
        while (true) {
            var x = ThreadLocalRandom.current().nextInt(xMin, xMax + 1);
            var z = ThreadLocalRandom.current().nextInt(zMin, zMax + 1);
            var pos = new BlockPos(x, 200, z);
            var biome = world.getBiome(pos);

            if (biome.unwrapKey().isPresent() && biome.unwrapKey().get() != Biomes.OCEAN && biome.unwrapKey().get() != Biomes.BEACH)
                return pos;
        }
    }

    public BlockPos findTeamSpawn(HashMap<Team, BlockPos> spawns, ServerLevel world, Vec2 center, float spreadDistance, float maxRange, int maxY) {
        var random = RandomSource.create();
        var attempts = 0;
        while (attempts < 100) {
            attempts++;
            var x = Math.floor(Mth.nextDouble(random, center.x - maxRange, center.x + maxRange));
            var z = Math.floor(Mth.nextDouble(random, center.y - maxRange, center.y + maxRange));

            // check if point is withing spread distance to another spawn
            var tooClose = spawns.values().stream().anyMatch(spawn -> {
                var distance = Math.sqrt(Math.pow(x - spawn.getX(), 2) + Math.pow(z - spawn.getZ(), 2));
                return distance < spreadDistance;
            });
            if (tooClose) continue;

            // get the top block
            var mutable = new BlockPos.MutableBlockPos(x, maxY + 1, z);
            var headValid = world.getBlockState(mutable).isAir();
            mutable.move(Direction.DOWN);
            var footValid = world.getBlockState(mutable).isAir();
            if (!headValid || !footValid) continue;

            while (world.getBlockState(mutable).isAir() && mutable.getY() > world.getMinY())
                mutable.move(Direction.DOWN);

            // check if top block is valid
            var state = world.getBlockState(mutable);
            //noinspection deprecation
            if (state.liquid() || state.is(BlockTags.FIRE)) continue;

            return mutable.move(Direction.UP);
        }
        return null;
    }

    public BingoItem getSlot(int row, int col) {
        return Card.getSlot(row, col);
    }

    public ItemStack getMap() {
        return Card.getMap();
    }

    public void addNewPlayer(ServerPlayer player, PlayerTeam team) {
        var scoreboard = Server.getScoreboard();
        scoreboard.addPlayerToTeam(player.getName().getString(), team);
        if (!BingoManager.BingoPlayers.contains(player.getUUID()))
            BingoManager.BingoPlayers.add(player.getUUID());
        var text = TranslationHelper.getAsText("dssb.game.team_joined", PlayerHelper.getPlayerName(player), team.getName()).withStyle(team.getColor());
        MessageHelper.broadcastChat(Server.getPlayerList(), text);
        if (Status == GameStatus.Playing) {
            var server = player.level().getServer();
            player.getInventory().clearContent();
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, getMap());
            givePlayerStatusEffects(player, true);
            givePlayerEquipment(player, true);
            BingoManager.Game.teleportPlayerToTeamSpawn(
                    WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension),
                    player,
                    BingoManager.Game.TeamSpawns.get(team).relative(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset)
            );
        }
        else if (Status == GameStatus.Starting) {
            var server = player.level().getServer();
            player.getInventory().clearContent();
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, getMap());
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300 * 20, 255, false, false, false));
            player.setNoGravity(true);
            BingoManager.Game.teleportPlayerToTeamSpawn(
                    WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension),
                    player,
                    BingoManager.Game.TeamSpawns.get(team).relative(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset)
            );
        }
    }

    public void buildBingoBoard(ServerLevel world, BlockPos pos) {
        var frames = world.getEntities(EntityType.GLOW_ITEM_FRAME, (glowItemFrame) -> pos.closerThan(glowItemFrame.blockPosition(), 20));
        for (var frame : frames)
            frame.remove(Entity.RemovalReason.DISCARDED);

        for (var i = 0; i < Card.size; i++) {
            for (var j = 0; j < Card.size; j++) {
                var slot = Card.slots[i][j];
                var framePos = pos.relative(Direction.Axis.Y, Card.size - 1 - i).relative(Direction.EAST, j);
                var frame = new GlowItemFrame(world, framePos.relative(Direction.SOUTH, 1), Direction.SOUTH);
                frame.setInvulnerable(true);
                frame.setItem(new ItemStack(slot.item, 1), true);
                world.setBlockAndUpdate(framePos, Blocks.BLACK_CONCRETE.defaultBlockState());
                world.addFreshEntity(frame);
            }
        }
    }

    public void runAfterRespawn(ServerPlayer player) {
        if (Status == GameStatus.Playing && BingoManager.BingoPlayers.contains(player.getUUID())) {
            givePlayerEquipment(player, true);
            givePlayerStatusEffects(player, true);
            player.getInventory().setItem(Inventory.SLOT_OFFHAND, getMap());
        } else if (BingoMod.CONFIG.SpawnSettings.TeleportToHubOnRespawn) {
            var world = WorldHelper.getWorldRegistryKeyByName(player.level().getServer(), BingoMod.CONFIG.SpawnSettings.Dimension);
            player.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(world, BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos(), 0.0f, 0.0f), true), false);
            BingoManager.tpToBingoSpawn(player);
        }
    }

    public void givePlayerEquipment(ServerPlayer player, boolean respawn) {
        var team = player.getTeam();
        if (team == null) return;

        for (var gear : BingoManager.GameSettings.StartingGear) {
            if (!gear.OnRespawn && respawn) continue;

            var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(gear.Name));
            var stack = new ItemStack(item, gear.Amount);
            if (stack.isEnchantable()) {
                var server = player.level().getServer();
                for (var enchantment : gear.Enchantments) {
                    var entry = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Identifier.parse(enchantment.Type));
                    entry.ifPresent(enchantmentReference -> stack.enchant(enchantmentReference, enchantment.Level));
                }
            }

            if (gear.AutoEquip) {
                var slot = player.getEquipmentSlotForItem(stack);
                player.setItemSlot(slot, stack);
            } else {
                player.addItem(stack);
            }
        }
    }

    public void givePlayerStatusEffects(Player player, boolean respawn) {
        var team = player.getTeam();
        if (team == null) return;

        player.removeAllEffects();

        for (var entry : BingoManager.GameSettings.Effects) {
            if (!entry.OnRespawn && respawn) continue;
            var effect = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(entry.Type));
            effect.ifPresent(statusEffectReference ->
                player.addEffect(new MobEffectInstance(statusEffectReference, entry.Duration < 0 ? -1 : entry.Duration * 20, entry.Amplifier, entry.Ambient, entry.ShowParticles, entry.ShowIcon))
            );
        }
    }

    public void clarify(ServerPlayer player, int rowIndex, int columnIndex) {
        var item = getSlot(rowIndex, columnIndex);

        Component text;
        if (item == null)
            text = TranslationHelper.getAsText("dssb.error.clarify_fail",rowIndex + 1, columnIndex + 1);
        else
            text = TranslationHelper.getAsText("dssb.game.clarify",rowIndex + 1, columnIndex + 1, item.item.getName().getString());

        if (player != null)
            player.sendSystemMessage(text);
    }

    public void setScoreboardStats(int teamNumber){
        var scoreboard = Server.getScoreboard();
        var updatePending = scoreboard.getObjective("bingo_update_pending");
        var winningTeam = scoreboard.getObjective("bingo_winning_team");
        var gamesPlayed = scoreboard.getObjective("bingo_games_played");
        var win = scoreboard.getObjective("bingo_win");
        var loss = scoreboard.getObjective("bingo_loss");
        var percentage = scoreboard.getObjective("bingo_percentage");

        if (updatePending == null || winningTeam == null || gamesPlayed == null || win == null || loss == null || percentage == null )
            return;

        // Set overall #bingo statistics
        var scoreHolder = ScoreHolder.forNameOnly("#bingo");
        var updateScore = scoreboard.getOrCreatePlayerScore(scoreHolder, updatePending);
        var winningScore = scoreboard.getOrCreatePlayerScore(scoreHolder, winningTeam);
        var totalPlayedScore = scoreboard.getOrCreatePlayerScore(scoreHolder, gamesPlayed);
        winningScore.set(teamNumber);
        updateScore.set(1);
        totalPlayedScore.increment();

        // Set player specific statistics
        for (var player : BingoManager.getValidPlayers(Server.getPlayerList())) {
            var playerTeam = player.getTeam();
            if (playerTeam != null) {
                var playerScoreHolder = ScoreHolder.fromGameProfile(player.getGameProfile());
                var playedScore = scoreboard.getOrCreatePlayerScore(playerScoreHolder, gamesPlayed);
                var winScore = scoreboard.getOrCreatePlayerScore(playerScoreHolder, win);
                var lossScore = scoreboard.getOrCreatePlayerScore(playerScoreHolder, loss);
                var percentageScore = scoreboard.getOrCreatePlayerScore(playerScoreHolder, percentage);

                playedScore.increment();
                if (getTeamNumber(playerTeam) == teamNumber)
                    winScore.increment();
                else
                    lossScore.increment();
                if (playedScore.get() > 0)
                    percentageScore.set((int)(((double)winScore.get()) / playedScore.get() * 100));
            }
        }
    }

    protected BlockState getConcrete(Team team) {
        for (var configTeam : BingoMod.CONFIG.Teams) {
            if (configTeam.Name.equals(team.getName()))
                return BuiltInRegistries.BLOCK.getValue(Identifier.parse(configTeam.BlockId)).defaultBlockState();
        }
        return null;
    }

    protected int getTeamNumber(Team team) {
        for (var configTeam : BingoMod.CONFIG.Teams) {
            if (configTeam.Name.equals(team.getName()))
                return configTeam.Number;
        }
        return 0;
    }
}
