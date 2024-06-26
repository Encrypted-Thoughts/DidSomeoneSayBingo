package encrypted.dssb;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.command.BingoSettingsCommand;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.itempools.ItemGroup;
import encrypted.dssb.gamemode.*;
import encrypted.dssb.util.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BingoManager {

    public static GameModeBase Game;
    public static GamePreset GameSettings;
    public static ArrayList<Item> CurrentItems = new ArrayList<>();
    public static boolean GenerateInProgress = false;
    public static boolean VoteInProgress = false;
    public static ArrayList<String> VotesToEnd;
    public static long VoteStart;
    public static ArrayList<UUID> BingoPlayers = new ArrayList<>();

    public static void start(ServerPlayerEntity starter, MinecraftServer server) {
        if (Game == null) {
            MessageHelper.sendSystemMessage(starter, TranslationHelper.getAsText("dssb.error.no_bingo_card"));
            return;
        }

        if (Game.Status != GameStatus.Idle) {
            MessageHelper.sendSystemMessage(starter, TranslationHelper.getAsText("dssb.error.in_progress"));
            return;
        }

        if (BingoManager.getValidPlayers(server.getPlayerManager()).isEmpty()) {
            MessageHelper.sendSystemMessage(starter, TranslationHelper.getAsText("dssb.error.no_players"));
            return;
        }

        Game.start();
    }

    public static ArrayList<ServerPlayerEntity> getValidPlayers(PlayerManager playerManager) {
        var validPlayers = new ArrayList<ServerPlayerEntity>();
        for (var player : playerManager.getPlayerList()) {
            if (BingoPlayers.contains(player.getUuid()))
                validPlayers.add(player);
        }
        return validPlayers;
    }

    public static ArrayList<ItemGroup> generateItemPool() {
        var possibleItems = new ArrayList<ItemGroup>();
        for (var name : GameSettings.ItemPools) {
            for (var pool : BingoMod.ItemPools) {
                if (pool.Name.equals(name))
                    possibleItems.addAll(pool.Items);
            }
        }
        return possibleItems;
    }

    public static ArrayList<Item> randomlySelectItems(ArrayList<ItemGroup> possibleItems, int amount) {
        var items = new ArrayList<Item>();
        var attempts = 0;
        var count = 0;
        while (count < amount) {
            attempts++;
            var group = getRandomItem(possibleItems);
            var random = ThreadLocalRandom.current().nextInt(0, group.Items.length);
            var possible = group.Items[random];
            var item = Registries.ITEM.get(Identifier.of(possible));

            if (GameSettings.StartingGear.stream().noneMatch(gear -> gear.Name.equals(possible)) || attempts > 100) {
                items.add(item);
                possibleItems.remove(group);
                count++;
            }
        }
        return items;
    }

    public static void generate(ServerPlayerEntity player, MinecraftServer server, boolean start) throws CommandSyntaxException {
        if (GenerateInProgress)
            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.generating"));
        else if (Game != null && Game.Status != GameStatus.Idle)
            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.in_progress"));
        else {
            GenerateInProgress = true;
            try {
                var possibleItems = generateItemPool();

                if (possibleItems.size() < 25) {
                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.not_enough_items"));
                    GenerateInProgress = false;
                    return;
                }

                CurrentItems = randomlySelectItems(possibleItems, 25);

                Game = switch (GameSettings.GameMode.toLowerCase()) {
                    case "lockout" -> new Lockout(server, CurrentItems);
                    case "blackout" -> new Blackout(server, CurrentItems);
                    case "hidden" -> new HiddenBingo(server, CurrentItems, HiddenBingo.HiddenType.DoubleDiagonal, 120);
                    default -> new Bingo(server, CurrentItems);
                };
                var spawnWorld = WorldHelper.getWorldByName(server, BingoMod.CONFIG.SpawnSettings.Dimension);
                if (spawnWorld != null) Game.buildBingoBoard(spawnWorld, BingoMod.CONFIG.DisplayBoardCoords.getBlockPos());
                if (start) start(player, server);
            } catch (Exception e) {
                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.generic_generation_failure"));
                BingoMod.LOGGER.error(e.getMessage());
            }
            GenerateInProgress = false;
        }
    }

    public static void end(MinecraftServer server) {
        if (Game != null) Game.end();
        tpAllToBingoSpawn(server);
    }

    public static void setGameMode(ServerPlayerEntity player, MinecraftServer server, String mode) {
        if (Game.Status == GameStatus.Idle) {
            if (CurrentItems.size() < 25) {
                try {
                    generate(player, server, false);
                } catch (CommandSyntaxException e) {
                    BingoMod.LOGGER.error(e.getMessage());
                }
            }
            try {
                MutableText text = null;
                switch (mode.toLowerCase()) {
                    case "bingo" -> {
                        text = TranslationHelper.getAsText("dssb.game.game_mode_set").append(TranslationHelper.getAsText("dssb.game.regular_bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Bingo";
                        Game = new Bingo(server, CurrentItems);
                    }
                    case "lockout" -> {
                        text = TranslationHelper.getAsText("dssb.game.game_mode_set").append(TranslationHelper.getAsText("dssb.game.lockout_bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Lockout";
                        Game = new Lockout(server, CurrentItems);
                    }
                    case "hidden" -> {
                        text = TranslationHelper.getAsText("dssb.game.game_mode_set").append(TranslationHelper.getAsText("dssb.game.hidden_bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Hidden";
                        Game = new HiddenBingo(server, CurrentItems, HiddenBingo.HiddenType.DoubleDiagonal, 120);
                    }
                    case "blackout" -> {
                        text = TranslationHelper.getAsText("dssb.game.game_mode_set").append(TranslationHelper.getAsText("dssb.game.blackout_bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Blackout";
                        Game = new Blackout(server, CurrentItems);
                    }
                }
                MessageHelper.broadcastChat(server.getPlayerManager(), text);
                generate(player, server, false);
            } catch (Exception e) {
                BingoMod.LOGGER.error("Failed to broadcast message to players on gamemode change.");
                BingoMod.LOGGER.error(e.getMessage());
            }
        } else {
            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.change_game_mode"));
        }
    }

    public static void checkItem(ServerPlayerEntity player, ItemStack itemStack) {
        if (Game.Status == GameStatus.Playing) {
            if (Game.checkItem(itemStack.getItem(), player) && Game.checkBingo(player.getScoreboardTeam())) {
                var server = player.getServer();
                if (server != null) {
                    tpAllToBingoSpawn(server);
                    for (var somePlayer : getValidPlayers(server.getPlayerManager())) {
                        var scoreboard = player.getScoreboardTeam();
                        if (scoreboard != null) {
                            var text = TranslationHelper.getAsText("dssb.game.game_over").formatted(player.getScoreboardTeam().getColor());
                            player.networkHandler.sendPacket(new TitleS2CPacket(text));
                            somePlayer.playSoundToPlayer(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 0.5f, 1);
                        }
                    }
                    Game.Status = GameStatus.Idle;
                }
            }
        }
    }

    public static void resetPlayers(MinecraftServer server) {
        for (var player : getValidPlayers(server.getPlayerManager()))
            resetPlayer(player);
    }

    public static void resetPlayer(ServerPlayerEntity player) {
        player.setMovementSpeed(1);
        player.setVelocity(0, 0, 0);
        player.clearStatusEffects();

        player.playerScreenHandler.clearCraftingSlots();
        player.currentScreenHandler.setCursorStack(new ItemStack(Items.AIR));
        player.getInventory().clear();
        player.currentScreenHandler.sendContentUpdates();
        player.playerScreenHandler.onContentChanged(player.getInventory());

        if (Game != null)
            player.getInventory().insertStack(Game.getMap());
        player.heal(player.getMaxHealth());
        player.getHungerManager().setFoodLevel(20);
        player.changeGameMode(net.minecraft.world.GameMode.byName(BingoMod.CONFIG.SpawnSettings.HubMode.toLowerCase()));
        var server = player.getServer();
        if (server != null) {
            var world = WorldHelper.getWorldRegistryKeyByName(player.getServer(), BingoMod.CONFIG.SpawnSettings.Dimension);
            player.setSpawnPoint(world, BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos(), 0, true, false);
        }
    }

    public static void tpAllToBingoSpawn(MinecraftServer server) {
        for (var player : getValidPlayers(server.getPlayerManager()))
            tpToBingoSpawn(player);
    }

    public static void tpToBingoSpawn(ServerPlayerEntity player) {
        try {
            var server = player.getServer();
            if (server != null) {
                var world = WorldHelper.getWorldByName(server, BingoMod.CONFIG.SpawnSettings.Dimension);
                var tpPlayer = TeleportHelper.teleport(
                        player,
                        world,
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getX() + 0.5,
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getY(),
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getZ() + 0.5,
                        180,
                        0);
                resetPlayer(tpPlayer);
            } else
                BingoMod.LOGGER.error("Unable to teleport player: %s to spawn".formatted(PlayerHelper.getPlayerName(player)));
        } catch (CommandSyntaxException e) {
            BingoMod.LOGGER.error("Unable to teleport player: %s to spawn".formatted(PlayerHelper.getPlayerName(player)));
            BingoMod.LOGGER.error(e.getMessage());
        }
    }

    public static ItemGroup getRandomItem(ArrayList<ItemGroup> items) {
        var values = items.stream().mapToDouble(item -> item.Weight).toArray();
        var totalWeight = Arrays.stream(values).sum();
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < values.length - 1; ++idx) {
            r -= values[idx];
            if (r <= 0.0) break;
        }
        return items.get(idx);
    }

    public static void randomizeTeams(MinecraftServer server) {
        var players = getValidPlayers(server.getPlayerManager());
        var teams = server.getScoreboard().getTeams();
        var playerCount = players.size();

        int numTeams = -1;
        for (var i = 2; i <= teams.size(); i++) {
            if (playerCount % i == 0) {
                numTeams = i;
                break;
            }
        }

        if (numTeams == -1 && playerCount < 9)
            numTeams = playerCount;

        if (numTeams == -1)
            numTeams = ThreadLocalRandom.current().nextInt(0, teams.size()) + 1;

        assignRandomTeams(players, server.getScoreboard(), numTeams);
    }

    public static void assignRandomTeams(List<ServerPlayerEntity> players, ServerScoreboard scoreboard, int teamNum) {

        Collections.shuffle(players);
        var groups = IntStream.range(0, players.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i % teamNum))
                .values()
                .stream()
                .map(il -> il.stream().map(players::get).collect(Collectors.toList()))
                .toList();

        var teams = new ArrayList<>(scoreboard.getTeams().stream().toList());
        for (var group : groups) {
            var index = ThreadLocalRandom.current().nextInt(0, teams.size());
            var team = teams.get(index);
            for (var player : group) {
                scoreboard.clearTeam(player.getName().getString());
                scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
            }
            teams.remove(index);
        }
    }

    public static void teamTP(ServerPlayerEntity player, MinecraftServer server) throws CommandSyntaxException {
        if (Game.Status == GameStatus.Playing) {
            var players = getValidPlayers(server.getPlayerManager());
            var teammates = new ArrayList<ServerPlayerEntity>();

            for (var target : players) {
                if (target.isTeammate(player) && target.getPos().distanceTo(player.getPos()) > 50)
                    teammates.add(target);
            }

            if (teammates.isEmpty()) {
                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.no_teammate"));
                return;
            }

            int randomNum = ThreadLocalRandom.current().nextInt(0, teammates.size());
            var tpTarget = teammates.get(randomNum);

            TeleportHelper.teleport(player, tpTarget.getServerWorld(), tpTarget.getX(), tpTarget.getY(), tpTarget.getZ(), 0, 0);

            MessageHelper.sendSystemMessage(teammates.get(randomNum), TranslationHelper.getAsText("dssb.game.teleport_target", PlayerHelper.getPlayerName(player)));
            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.teleport_to", PlayerHelper.getPlayerName(teammates.get(randomNum))));
        }
    }

    public static void clarify(ServerCommandSource source, int rowIndex, int columnIndex) {
        if (Game != null)
            Game.clarify(source.getPlayer(), rowIndex, columnIndex);
    }

    public static void runOnStartup(MinecraftServer server) {
        createTeams(server);
    }

    public static void createTeams(MinecraftServer server) {
        var scoreboard = server.getScoreboard();
        var teams = scoreboard.getTeamNames();

        for (var configTeam : BingoMod.CONFIG.Teams) {
            if (!teams.contains(configTeam.Name))
                scoreboard.addTeam(configTeam.Name);

            var team = scoreboard.getTeam(configTeam.Name);
            if (team != null) {
                team.setColor(configTeam.Color);
                team.setCollisionRule(configTeam.Collision);
                team.setFriendlyFireAllowed(configTeam.FriendlyFire);
            }
        }
    }

    public static void runOnServerTickEvent(MinecraftServer server) {
        if (Game == null) return;
        switch (Game.Status) {
            case Loading -> {
                var text = TranslationHelper.getAsText("dssb.game.loading_spawns", Game.TeamSpawns.size());
                MessageHelper.broadcastOverlay(server.getPlayerManager(), text);
            }
            case Initializing -> {
                var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
                if (world == null) return;

                for (var tempWorld : server.getWorlds()) {
                    tempWorld.setTimeOfDay(1000);
                    tempWorld.setWeather(new Random().nextInt(0, 1000000), 0, false, false);
                }

                Game.teleportPlayersToTeamSpawns(world);
                Game.CountDownStart = System.currentTimeMillis();
                Game.CurrentCountdownSecond = 0;

                var text = TranslationHelper.getAsText("dssb.game.clarify_info");
                MessageHelper.broadcastChatToPlayers(server.getPlayerManager(), text);
                text = TranslationHelper.getAsText("dssb.game.team_tp_info");
                MessageHelper.broadcastChatToPlayers(server.getPlayerManager(), text);
                text = TranslationHelper.getAsText("dssb.game.get_map_info");
                MessageHelper.broadcastChatToPlayers(server.getPlayerManager(), text);
                text = TranslationHelper.getAsText("dssb.game.vote_end_info");
                MessageHelper.broadcastChatToPlayers(server.getPlayerManager(), text);

                Game.Status = GameStatus.Starting;
            }
            case Starting -> {
                for (var team : Game.TeamSpawns.entrySet()) {
                    var teamPos = team.getValue().offset(Direction.Axis.Y, GameSettings.YSpawnOffset);
                    for (var player : getValidPlayers(server.getPlayerManager())) {
                        if (player.isTeamPlayer(team.getKey()) && !player.getBlockPos().equals(teamPos)) {
                            try {
                                var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
                                TeleportHelper.teleport(player, world, teamPos.getX() + 0.5, teamPos.getY(), teamPos.getZ() + 0.5, player.getYaw(), player.getPitch());
                            } catch (CommandSyntaxException e) {
                                BingoMod.LOGGER.error("Unable to teleport player: %s".formatted(PlayerHelper.getPlayerName(player)));
                            }
                        }
                    }
                }
                Game.handleCountdown();
            }
            case Playing -> {
                if (Game.TimerRunning) Game.handleTimer();
                if (VoteInProgress) checkVoteProgress(server.getPlayerManager());
            }
        }
    }

    private static void checkVoteProgress(PlayerManager playerManager) {
        var elapsedSeconds = (System.currentTimeMillis() - VoteStart) / 1000;

        if (elapsedSeconds > 30) {
            MessageHelper.broadcastChatToPlayers(playerManager, TranslationHelper.getAsText("dssb.game.not_enough_votes"));
            VoteInProgress = false;
        }
    }

    public static void handleVote(ServerPlayerEntity player, MinecraftServer server) {
        if (Game.Status != GameStatus.Idle) {
            var playerManager = server.getPlayerManager();
            var majority = getValidPlayers(playerManager).size() / 2;

            if (majority < 2) {
                MessageHelper.broadcastChatToPlayers(playerManager, TranslationHelper.getAsText("dssb.game.vote_game_ended", PlayerHelper.getPlayerName(player)));
                end(server);
                VoteInProgress = false;
                VotesToEnd = new ArrayList<>();
            } else {
                if (VoteInProgress) {
                    if (VotesToEnd.contains(player.getName().getString()))
                        MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.vote_once").formatted(Formatting.RED));
                    else {
                        VotesToEnd.add(player.getName().getString());
                        if (VotesToEnd.size() >= majority) {
                            MessageHelper.broadcastChatToPlayers(playerManager, TranslationHelper.getAsText("dssb.game.vote_game_ended", PlayerHelper.getPlayerName(player)));
                            end(server);
                            VoteInProgress = false;
                        } else
                            MessageHelper.broadcastChatToPlayers(playerManager, TranslationHelper.getAsText("dssb.game.voted",  PlayerHelper.getPlayerName(player), majority - VotesToEnd.size()));
                    }
                } else {
                    MessageHelper.broadcastChatToPlayers(playerManager, TranslationHelper.getAsText("dssb.game.vote_started", PlayerHelper.getPlayerName(player), majority - 1));
                    VoteInProgress = true;
                    VotesToEnd = new ArrayList<>();
                    VotesToEnd.add(player.getName().getString());
                    VoteStart = System.currentTimeMillis();
                }
            }
        }
    }

    public static void runAfterPlayerRespawnEvent(ServerPlayerEntity player) {
        Game.runAfterRespawn(player);
    }

    public static void runOnPlayerConnectionEvent(ServerPlayerEntity player, MinecraftServer server) {
        if (Game == null && server != null) {
            try {
                var possibleItems = generateItemPool();
                if (possibleItems.size() < 25) {
                    BingoMod.LOGGER.warn("Not enough items in default item pool to initialize game items.");
                    return;
                }
                CurrentItems = randomlySelectItems(possibleItems, 25);
                Game = switch (GameSettings.GameMode.toLowerCase()) {
                    case "lockout" -> new Lockout(server, CurrentItems);
                    case "blackout" -> new Blackout(server, CurrentItems);
                    case "hidden" -> new HiddenBingo(server, CurrentItems, HiddenBingo.HiddenType.DoubleDiagonal, 120);
                    default -> new Bingo(server, CurrentItems);
                };
                var spawnWorld = WorldHelper.getWorldByName(server, BingoMod.CONFIG.SpawnSettings.Dimension);
                if (spawnWorld != null) Game.buildBingoBoard(spawnWorld, BingoMod.CONFIG.DisplayBoardCoords.getBlockPos());
            } catch (Exception e) {
                BingoMod.LOGGER.error("Failure during server startup for handling initial bingo generation.");
                BingoMod.LOGGER.error(e.getMessage());
            }
        }

        MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.welcome"));
        MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.join_team_info"));
        MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.game.help_info"));

        try {
            BingoSettingsCommand.tellSettings(player);
        } catch (CommandSyntaxException e) {
            BingoMod.LOGGER.error("Unable to tell player: %s the command options".formatted(PlayerHelper.getPlayerName(player)));
            BingoMod.LOGGER.error(e.getMessage());
        }

        if (!BingoPlayers.contains(player.getUuid())) {
            var scoreboard = player.getScoreboard();
            var team = player.getScoreboardTeam();
            if (BingoMod.CONFIG.AssignRandomTeamOnJoin && team == null) {
                var teams = new ArrayList<>(scoreboard.getTeams());
                var randomTeamIndex = ThreadLocalRandom.current().nextInt(0, teams.size());
                scoreboard.addScoreHolderToTeam(player.getName().getString(), teams.get(randomTeamIndex));
            } else if (!BingoMod.CONFIG.AssignRandomTeamOnJoin)
                player.getScoreboard().clearTeam(player.getName().getString());

            if (BingoMod.CONFIG.SpawnSettings.TeleportToHubOnJoin && player.isAlive())
                tpToBingoSpawn(player);
        } else if (Game.Status == GameStatus.Idle && BingoMod.CONFIG.SpawnSettings.TeleportToHubOnJoin && player.isAlive())
            tpToBingoSpawn(player);
    }
}
