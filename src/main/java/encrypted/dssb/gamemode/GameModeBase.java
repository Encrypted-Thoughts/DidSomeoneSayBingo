package encrypted.dssb.gamemode;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.model.BingoItem;
import encrypted.dssb.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GameModeBase {
    public boolean DirtyCard = false;
    public MinecraftServer Server;

    protected BingoCard Card;

    public HashMap<AbstractTeam, BlockPos> TeamSpawns = new HashMap<>();

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

        CompletableFuture.runAsync(() -> {
            for (var team : world.getScoreboard().getTeams()) {
                var pos = findTeamSpawn(
                        TeamSpawns,
                        world,
                        new Vec2f(origin.getX(), origin.getZ()),
                        100,
                        BingoManager.GameSettings.TPRandomizationRadius,
                        BingoManager.GameSettings.MaxYLevel);
                if (pos != null)
                    TeamSpawns.put(team, pos);
                else {
                    MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), TranslationHelper.getAsText("dssb.error.unable_to_find_spawn", team.getName()));
                    end();
                }
            }

            for (var player : BingoManager.getValidPlayers(Server.getPlayerManager())) {
                player.getInventory().clear();
                player.getInventory().offHand.set(0, Card.getMap());
                player.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
            }
            Status = GameStatus.Initializing;
        }).exceptionally(e -> {
            MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), TranslationHelper.getAsText("dssb.error.spawn_problem"));
            BingoMod.LOGGER.error(e.getMessage());
            return null;
        });
    }

    public boolean checkItem(Item item, PlayerEntity player) {
        var foundByTeam = player.getScoreboardTeam();
        var server = player.getServer();
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

                    final Text itemFound = TranslationHelper.getAsText("dssb.game.item_found", PlayerHelper.getPlayerName(player), item.getName().getString()).formatted(foundByTeam.getColor());
                    MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), itemFound);
                    playNotificationSound(player.getWorld());
                    return true;
                }
                colIndex++;
            }
            rowIndex++;
        }
        return false;
    }

    public void handleWin(AbstractTeam team) {
        TimerRunning = false;

        var timeDif = System.currentTimeMillis() - TimerStart;
        var millis = timeDif % 1000;
        var second = (timeDif / 1000) % 60;
        var minute = (timeDif / (1000 * 60)) % 60;
        var hour = (timeDif / (1000 * 60 * 60)) % 24;
        var readableTime = "";
        if (hour > 0) readableTime = String.format("%d:%02d:%02d.%d", hour, minute, second, millis);
        else readableTime = String.format("%d:%02d.%d", minute, second, millis);

        final Text bingoFinished = TranslationHelper.getAsText("dssb.game.team_wins",team.getName(), readableTime).formatted(team.getColor());
        MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), bingoFinished);

        var world = WorldHelper.getWorldByName(Server, BingoMod.CONFIG.SpawnSettings.Dimension);
        if (world != null) {
            for (var i = 0; i < Card.size; i++) {
                for (var j = 0; j < Card.size; j++) {
                    var slot = Card.slots[i][j];
                    var framePos = BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().offset(Direction.Axis.Y, Card.size - 1 - i).offset(Direction.EAST, j);
                    if (slot.teams.contains(team))
                        world.setBlockState(framePos, getConcrete(team));
                }
            }
        }
        setScoreboardStats(getTeamNumber(team));

        Status = GameStatus.Idle;
    }

    public void playNotificationSound(World world) {
        for (var player : world.getPlayers())
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);
    }

    public void teleportPlayersToTeamSpawns(ServerWorld world) {
        for (var team : TeamSpawns.entrySet()) {
            if (team.getKey() == null || team.getKey().getPlayerList().isEmpty())
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
                player.getHungerManager().setFoodLevel(20);
                TeleportHelper.teleport(player, world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0, 0);
                player.changeGameMode(net.minecraft.world.GameMode.SURVIVAL);
                player.setSpawnPoint(player.getWorld().getRegistryKey(), spawn, 0, true, false);
            } catch (CommandSyntaxException e) {
                BingoMod.LOGGER.error(e.getMessage());
            }
        }
    }

    public BlockPos getRandomPos(ServerWorld world, int xMin, int xMax, int zMin, int zMax) {
        while (true) {
            var x = ThreadLocalRandom.current().nextInt(xMin, xMax + 1);
            var z = ThreadLocalRandom.current().nextInt(zMin, zMax + 1);
            var pos = new BlockPos(x, 200, z);
            var biome = world.getBiome(pos);

            if (biome.getKey().isPresent() && biome.getKey().get() != BiomeKeys.OCEAN && biome.getKey().get() != BiomeKeys.BEACH)
                return pos;
        }
    }

    public BlockPos findTeamSpawn(HashMap<AbstractTeam, BlockPos> spawns, ServerWorld world, Vec2f center, float spreadDistance, float maxRange, int maxY) {
        var random = Random.create();
        var attempts = 0;
        while (attempts < 100) {
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
            var state = world.getBlockState(mutable);
            //noinspection deprecation
            if (state.isLiquid() || state.isIn(BlockTags.FIRE)) continue;

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

    public void addNewPlayer(ServerPlayerEntity player, Team team) {
        var scoreboard = Server.getScoreboard();
        scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
        if (!BingoManager.BingoPlayers.contains(player.getUuid()))
            BingoManager.BingoPlayers.add(player.getUuid());
        var text = TranslationHelper.getAsText("dssb.game.team_joined", PlayerHelper.getPlayerName(player), team.getName()).formatted(team.getColor());
        MessageHelper.broadcastChat(Server.getPlayerManager(), text);
        if (Status == GameStatus.Playing) {
            var server = player.getServer();
            if (server != null) {
                player.getInventory().clear();
                player.getInventory().offHand.set(0, getMap());
                givePlayerStatusEffects(player, true);
                givePlayerEquipment(player, true);
                BingoManager.Game.teleportPlayerToTeamSpawn(
                        WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension),
                        player,
                        BingoManager.Game.TeamSpawns.get(team).offset(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset)
                );
            }
        }
        else if (Status == GameStatus.Starting) {
            var server = player.getServer();
            if (server != null) {
                player.getInventory().clear();
                player.getInventory().offHand.set(0, getMap());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300 * 20, 255, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 300 * 20, 255, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 300 * 20, 250, false, false, false));
                BingoManager.Game.teleportPlayerToTeamSpawn(
                        WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension),
                        player,
                        BingoManager.Game.TeamSpawns.get(team).offset(Direction.Axis.Y, BingoManager.GameSettings.YSpawnOffset)
                );
            }
        }
    }

    public void buildBingoBoard(ServerWorld world, BlockPos pos) {
        var frames = world.getEntitiesByType(EntityType.GLOW_ITEM_FRAME, (glowItemFrame) -> pos.isWithinDistance(glowItemFrame.getBlockPos(), 20));
        for (var frame : frames)
            frame.remove(Entity.RemovalReason.DISCARDED);

        for (var i = 0; i < Card.size; i++) {
            for (var j = 0; j < Card.size; j++) {
                var slot = Card.slots[i][j];
                var framePos = pos.offset(Direction.Axis.Y, Card.size - 1 - i).offset(Direction.EAST, j);
                var frame = new GlowItemFrameEntity(world, framePos.offset(Direction.SOUTH, 1), Direction.SOUTH);
                frame.setInvulnerable(true);
                frame.setHeldItemStack(new ItemStack(slot.item, 1), true);
                world.setBlockState(framePos, Blocks.BLACK_CONCRETE.getDefaultState());
                world.spawnEntity(frame);
            }
        }
    }

    public void runAfterRespawn(ServerPlayerEntity player) {
        if (Status == GameStatus.Playing && BingoManager.BingoPlayers.contains(player.getUuid())) {
            givePlayerEquipment(player, true);
            givePlayerStatusEffects(player, true);
            player.getInventory().offHand.set(0, getMap());
        } else if (BingoMod.CONFIG.SpawnSettings.TeleportToHubOnRespawn) {
            var server = player.getServer();
            if (server != null) {
                var world = WorldHelper.getWorldRegistryKeyByName(player.getServer(), BingoMod.CONFIG.SpawnSettings.Dimension);
                player.setSpawnPoint(world, BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos(), 0, true, false);
                BingoManager.tpToBingoSpawn(player);
            }
        }
    }

    public void givePlayerEquipment(PlayerEntity player, boolean respawn) {
        var team = player.getScoreboardTeam();
        if (team == null) return;

        for (var gear : BingoManager.GameSettings.StartingGear) {
            if (!gear.OnRespawn && respawn) continue;

            var item = Registries.ITEM.get(new Identifier(gear.Name));
            var stack = new ItemStack(item, gear.Amount);
            if (stack.isEnchantable()) {
                for (var enchantment : gear.Enchantments)
                    stack.addEnchantment(Registries.ENCHANTMENT.get(new Identifier(enchantment.Type)), enchantment.Level);
            }

            if (gear.AutoEquip) {
                var slot = LivingEntity.getPreferredEquipmentSlot(stack);
                player.equipStack(slot, stack);
            } else {
                player.giveItemStack(stack);
            }
        }
    }

    public void givePlayerStatusEffects(PlayerEntity player, boolean respawn) {
        var team = player.getScoreboardTeam();
        if (team == null) return;

        player.clearStatusEffects();

        for (var entry : BingoManager.GameSettings.Effects) {
            if (!entry.OnRespawn && respawn) continue;
            var effect = Registries.STATUS_EFFECT.getEntry(new Identifier(entry.Type));
            effect.ifPresent(statusEffectReference ->
                player.addStatusEffect(new StatusEffectInstance(statusEffectReference, entry.Duration < 0 ? -1 : entry.Duration * 20, entry.Amplifier, entry.Ambient, entry.ShowParticles, entry.ShowIcon))
            );
        }
    }

    public void clarify(ServerPlayerEntity player, int rowIndex, int columnIndex) {
        var item = getSlot(rowIndex, columnIndex);

        Text text;
        if (item == null)
            text = TranslationHelper.getAsText("dssb.error.clarify_fail",rowIndex + 1, columnIndex + 1);
        else
            text = TranslationHelper.getAsText("dssb.game.clarify",rowIndex + 1, columnIndex + 1, item.item.getName().getString());

        if (player != null)
            player.sendMessage(text);
    }

    public void setScoreboardStats(int teamNumber){
        var scoreboard = Server.getScoreboard();
        var updatePending = scoreboard.getNullableObjective("bingo_update_pending");
        var winningTeam = scoreboard.getNullableObjective("bingo_winning_team");
        var gamesPlayed = scoreboard.getNullableObjective("bingo_games_played");
        var win = scoreboard.getNullableObjective("bingo_win");
        var loss = scoreboard.getNullableObjective("bingo_loss");
        var percentage = scoreboard.getNullableObjective("bingo_percentage");

        if (updatePending == null || winningTeam == null || gamesPlayed == null || win == null || loss == null || percentage == null )
            return;

        // Set overall #bingo statistics
        var scoreHolder = ScoreHolder.fromName("#bingo");
        var updateScore = scoreboard.getOrCreateScore(scoreHolder, updatePending);
        var winningScore = scoreboard.getOrCreateScore(scoreHolder, winningTeam);
        var totalPlayedScore = scoreboard.getOrCreateScore(scoreHolder, gamesPlayed);
        if (updateScore != null && winningScore != null && totalPlayedScore != null) {
            winningScore.setScore(teamNumber);
            updateScore.setScore(1);
            totalPlayedScore.incrementScore();
        }

        // Set player specific statistics
        for (var player : BingoManager.getValidPlayers(Server.getPlayerManager())) {
            var playerTeam = player.getScoreboardTeam();
            if (playerTeam != null) {
                var playerScoreHolder = ScoreHolder.fromProfile(player.getGameProfile());
                var playedScore = scoreboard.getOrCreateScore(playerScoreHolder, gamesPlayed);
                var winScore = scoreboard.getOrCreateScore(playerScoreHolder, win);
                var lossScore = scoreboard.getOrCreateScore(playerScoreHolder, loss);
                var percentageScore = scoreboard.getOrCreateScore(playerScoreHolder, percentage);

                if (playedScore == null || winScore == null || lossScore == null || percentageScore == null)
                    continue;

                playedScore.incrementScore();
                if (getTeamNumber(playerTeam) == teamNumber)
                    winScore.incrementScore();
                else
                    lossScore.incrementScore();
                if (playedScore.getScore() > 0)
                    percentageScore.setScore((int)(((double)winScore.getScore()) / playedScore.getScore() * 100));
            }
        }
    }

    protected BlockState getConcrete(AbstractTeam team) {
        for (var configTeam : BingoMod.CONFIG.Teams) {
            if (configTeam.Name.equals(team.getName()))
                return Registries.BLOCK.get(new Identifier(configTeam.BlockId)).getDefaultState();
        }
        return null;
    }

    protected int getTeamNumber(AbstractTeam team) {
        for (var configTeam : BingoMod.CONFIG.Teams) {
            if (configTeam.Name.equals(team.getName()))
                return configTeam.Number;
        }
        return 0;
    }
}
