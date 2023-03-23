package encrypted.dssb;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.command.BingoSettingsCommand;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.itempools.ItemGroup;
import encrypted.dssb.gamemode.*;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TeleportHelper;
import encrypted.dssb.util.WorldHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BingoManager {

    public static GameMode Game;
    public static GamePreset GameSettings;
    public static ArrayList<Item> CurrentItems = new ArrayList<>();
    public static boolean GenerateInProgress = false;
    public static boolean VoteInProgress = false;
    public static ArrayList<String> VotesToEnd;
    public static long VoteStart;
    public static ArrayList<UUID> BingoPlayers = new ArrayList<>();

    public static int start(ServerPlayerEntity starter, MinecraftServer server) throws CommandSyntaxException {
        if (Game == null) {
            MessageHelper.sendSystemMessage(starter, Text.literal("No bingo card has been generated yet since last server restart. Please generate a new card first.").formatted(Formatting.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (Game.Status != GameStatus.Idle) {
            MessageHelper.sendSystemMessage(starter, Text.literal("Game of Bingo already in progress.").formatted(Formatting.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (BingoManager.getValidPlayers(server.getPlayerManager()).size() == 0) {
            MessageHelper.sendSystemMessage(starter, Text.literal("No players on any teams to play the game.").formatted(Formatting.RED));
            return Command.SINGLE_SUCCESS;
        }

        Game.start();
        return Command.SINGLE_SUCCESS;
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
            var item = Registries.ITEM.get(new Identifier(possible));

            if (GameSettings.StartingGear.stream().noneMatch(gear -> gear.Name.equals(possible)) || attempts > 100) {
                items.add(item);
                possibleItems.remove(group);
                count++;
            }
        }
        return items;
    }

    public static int generate(ServerPlayerEntity player, MinecraftServer server, boolean start) throws CommandSyntaxException {
        if (GenerateInProgress)
            MessageHelper.sendSystemMessage(player, Text.literal("Still generating previous board.").formatted(Formatting.RED));
        else if (Game != null && Game.Status != GameStatus.Idle)
            MessageHelper.sendSystemMessage(player, Text.literal("Game already in progress.").formatted(Formatting.RED));
        else {
            GenerateInProgress = true;
            try {
                var possibleItems = generateItemPool();

                if (possibleItems.size() < 25) {
                    MessageHelper.sendSystemMessage(player, Text.literal("Not enough enough possible items in item pools to generate a bingo card.").formatted(Formatting.RED));
                    GenerateInProgress = false;
                    return Command.SINGLE_SUCCESS;
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
                MessageHelper.sendSystemMessage(player, Text.literal("Unable to generate board check system console for more information.").formatted(Formatting.RED));
                e.printStackTrace();
            }
            GenerateInProgress = false;
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int end(MinecraftServer server) {
        if (Game != null) Game.end();
        tpAllToBingoSpawn(server);

        return Command.SINGLE_SUCCESS;
    }

    public static void setGameMode(ServerPlayerEntity player, MinecraftServer server, String mode) {
        if (Game.Status == GameStatus.Idle) {
            if (CurrentItems.size() < 25) {
                try {
                    generate(player, server, false);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            try {
                MutableText text = null;
                switch (mode.toLowerCase()) {
                    case "bingo" -> {
                        text = Text.literal("Game mode set to ").formatted(Formatting.WHITE).append(Text.literal("Regular Bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Bingo";
                        Game = new Bingo(server, CurrentItems);
                    }
                    case "lockout" -> {
                        text = Text.literal("Game mode set to ").formatted(Formatting.WHITE).append(Text.literal("Lockout Bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Lockout";
                        Game = new Lockout(server, CurrentItems);
                    }
                    case "hidden" -> {
                        text = Text.literal("Game mode set to ").formatted(Formatting.WHITE).append(Text.literal("Hidden Bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Hidden";
                        Game = new HiddenBingo(server, CurrentItems, HiddenBingo.HiddenType.DoubleDiagonal, 120);
                    }
                    case "blackout" -> {
                        text = Text.literal("Game mode set to ").formatted(Formatting.WHITE).append(Text.literal("Blackout Bingo").formatted(Formatting.GREEN));
                        GameSettings.GameMode = "Blackout";
                        Game = new Blackout(server, CurrentItems);
                    }
                }
                MessageHelper.broadcastChat(server.getPlayerManager(), text);
            } catch (Exception e) {
                BingoMod.LOGGER.error("Failed to broadcast message to players on gamemode change.");
                e.printStackTrace();
            }
        } else {
            MessageHelper.sendSystemMessage(player, Text.literal("Can't change game mode while game in progress.").formatted(Formatting.RED));
        }
    }

    public static void checkItem(ServerPlayerEntity player, Item item) {
        if (Game.Status == GameStatus.Playing) {
            if (Game.checkItem(item, player) && Game.checkBingo(player.getScoreboardTeam())) {
                var server = player.getServer();
                if (server != null) {
                    tpAllToBingoSpawn(server);
                    for (var somePlayer : getValidPlayers(server.getPlayerManager())) {
                        var scoreboard = player.getScoreboardTeam();
                        if (scoreboard != null) {
                            var text = Text.literal("BINGO").formatted(player.getScoreboardTeam().getColor());
                            player.networkHandler.sendPacket(new TitleS2CPacket(text));
                            somePlayer.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 0.5f, 1);
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

    public static void setPlayerSpawn(ServerPlayerEntity player) {
        var server = player.getServer();
        if (Game.Status != GameStatus.Idle && server != null) {
            var team = player.getScoreboardTeam();
            var spawn = Game.TeamSpawns.get(team).offset(Direction.Axis.Y, GameSettings.YSpawnOffset);
            var dimension = WorldHelper.getWorldRegistryKeyByName(server, BingoManager.GameSettings.Dimension);
            player.setSpawnPoint(dimension, spawn, 0, true, false);
        } else if (server != null) {
            var dimension = WorldHelper.getWorldRegistryKeyByName(server, BingoMod.CONFIG.SpawnSettings.Dimension);
            var spawn = BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos();
            player.setSpawnPoint(dimension, spawn, 0, true, false);
        }
    }

    public static void tpToBingoSpawn(ServerPlayerEntity player) {
        try {
            var server = player.getServer();
            if (server != null) {
                var world = WorldHelper.getWorldByName(server, BingoMod.CONFIG.SpawnSettings.Dimension);
                TeleportHelper.teleport(
                        player,
                        world,
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getX() + 0.5,
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getY(),
                        BingoMod.CONFIG.SpawnSettings.HubCoords.getBlockPos().getZ() + 0.5,
                        180,
                        0);
                resetPlayer(player);
            } else
                BingoMod.LOGGER.error("Unable to teleport player: %s to spawn".formatted(player.getDisplayName().getString()));
        } catch (CommandSyntaxException e) {
            BingoMod.LOGGER.error("Unable to teleport player: %s to spawn".formatted(player.getDisplayName().getString()));
            e.printStackTrace();
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
                scoreboard.clearPlayerTeam(player.getName().getString());
                scoreboard.addPlayerToTeam(player.getName().getString(), team);
            }
            teams.remove(index);
        }
    }

    public static int teamTP(ServerPlayerEntity player, MinecraftServer server) throws CommandSyntaxException {
        if (Game.Status == GameStatus.Playing) {
            var players = getValidPlayers(server.getPlayerManager());
            var teammates = new ArrayList<ServerPlayerEntity>();

            for (var target : players) {
                if (target.isTeammate(player) && target.getPos().distanceTo(player.getPos()) > 50)
                    teammates.add(target);
            }

            if (teammates.size() == 0) {
                MessageHelper.sendSystemMessage(player, Text.literal("No teammate more than 50 blocks away to tp to.").formatted(Formatting.RED));
                return Command.SINGLE_SUCCESS;
            }

            int randomNum = ThreadLocalRandom.current().nextInt(0, teammates.size());
            var tpTarget = teammates.get(randomNum);

            TeleportHelper.teleport(player, tpTarget.getWorld(), tpTarget.getX(), tpTarget.getY(), tpTarget.getZ(), 0, 0);

            MessageHelper.sendSystemMessage(teammates.get(randomNum), Text.literal("%s teleported to you.".formatted(player.getDisplayName().getString())).formatted(Formatting.GOLD));
            MessageHelper.sendSystemMessage(player, Text.literal("Teleported to %s.".formatted(teammates.get(randomNum).getDisplayName().getString())).formatted(Formatting.GOLD));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int clarify(ServerCommandSource source, int rowIndex, int columnIndex) {
        if (Game != null)
            Game.clarify(source.getPlayer(), rowIndex, columnIndex);

        return Command.SINGLE_SUCCESS;
    }

    public static void runOnStartup(MinecraftServer server) {
        createTeams(server);
    }

    public static void createTeams(MinecraftServer server) {
        var scoreboard = server.getScoreboard();
        var teams = scoreboard.getTeamNames();
        if (!teams.contains("Red")) {
            var team = scoreboard.addTeam("Red");
            team.setColor(Formatting.RED);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Green")) {
            var team = scoreboard.addTeam("Green");
            team.setColor(Formatting.GREEN);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Blue")) {
            var team = scoreboard.addTeam("Blue");
            team.setColor(Formatting.BLUE);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Yellow")) {
            var team = scoreboard.addTeam("Yellow");
            team.setColor(Formatting.YELLOW);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Cyan")) {
            var team = scoreboard.addTeam("Cyan");
            team.setColor(Formatting.AQUA);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Purple")) {
            var team = scoreboard.addTeam("Purple");
            team.setColor(Formatting.DARK_PURPLE);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Pink")) {
            var team = scoreboard.addTeam("Pink");
            team.setColor(Formatting.LIGHT_PURPLE);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
        if (!teams.contains("Orange")) {
            var team = scoreboard.addTeam("Orange");
            team.setColor(Formatting.GOLD);
            team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OWN_TEAM);
            team.setFriendlyFireAllowed(false);
        }
    }

    public static void runOnServerTickEvent(MinecraftServer server) {
        if (Game == null) return;
        switch (Game.Status) {
            case Loading -> {
                var text = Text.literal("Loading Team Spawns (%s/8)".formatted(Game.TeamSpawns.size())).formatted(Formatting.GREEN);
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

                var text = Text.literal("Use /clarify <row> <col> if unsure of what an item on the board is.").formatted(Formatting.WHITE);
                MessageHelper.broadcastChat(server.getPlayerManager(), text);
                text = Text.literal("Use /teamtp to teleport to a random teammate that's more than 50 blocks away.").formatted(Formatting.GOLD);
                MessageHelper.broadcastChat(server.getPlayerManager(), text);
                text = Text.literal("Use /bingo getmap if you lose your bingo map to get another one.").formatted(Formatting.WHITE);
                MessageHelper.broadcastChat(server.getPlayerManager(), text);
                text = Text.literal("Use /bingo voteend if you want to start a vote to end the current game in a tie.").formatted(Formatting.GOLD);
                MessageHelper.broadcastChat(server.getPlayerManager(), text);

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
                                BingoMod.LOGGER.error("Unable to teleport player: %s".formatted(player.getDisplayName().getString()));
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
            MessageHelper.broadcastChatToPlayers(playerManager, Text.literal("Not enough votes. Vote ended.").formatted(Formatting.GOLD));
            VoteInProgress = false;
        }
    }

    public static void handleVote(ServerPlayerEntity player, MinecraftServer server) {
        if (Game.Status != GameStatus.Idle) {
            var playerManager = server.getPlayerManager();
            var majority = getValidPlayers(playerManager).size() / 2;

            if (majority < 2) {
                MessageHelper.broadcastChatToPlayers(playerManager, Text.literal("%s has voted to end the game! Game ended.".formatted(player.getDisplayName().getString())).formatted(Formatting.GOLD));
                end(server);
                VoteInProgress = false;
                VotesToEnd = new ArrayList<>();
            } else {
                if (VoteInProgress) {
                    if (VotesToEnd.contains(player.getName().getString()))
                        MessageHelper.sendSystemMessage(player, Text.literal("You can only vote once.").formatted(Formatting.RED));
                    else {
                        VotesToEnd.add(player.getName().getString());
                        if (VotesToEnd.size() >= majority) {
                            MessageHelper.broadcastChatToPlayers(playerManager, Text.literal("%s has voted to end the game! Game ended.".formatted(player.getDisplayName().getString())).formatted(Formatting.GOLD));
                            end(server);
                            VoteInProgress = false;
                        } else
                            MessageHelper.broadcastChatToPlayers(playerManager, Text.literal("%s has voted to end the game! %s more votes needed.".formatted(player.getDisplayName().getString(), majority - VotesToEnd.size())).formatted(Formatting.GOLD));
                    }
                } else {
                    MessageHelper.broadcastChatToPlayers(playerManager, Text.literal("%s has started a vote to end the game! %s more votes needed to end. Use '/bingo voteend' to vote to end.".formatted(player.getDisplayName().getString(), majority - 1)).formatted(Formatting.GOLD));
                    VoteInProgress = true;
                    VotesToEnd = new ArrayList<>();
                    VotesToEnd.add(player.getName().getString());
                    VoteStart = System.currentTimeMillis();
                }
            }
        }
    }

    public static  void runAfterPlayerRespawnEvent(ServerPlayerEntity player) {
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
            } catch (Exception ex) {
                BingoMod.LOGGER.error("Failure during server startup for handling initial bingo generation.");
                ex.printStackTrace();
            }
        }

        MessageHelper.sendSystemMessage(player, Text.literal("Welcome to Bingo!").formatted(Formatting.GREEN));
        MessageHelper.sendSystemMessage(player, Text.literal("Right click a sign to join a team or do '/bingo team join <team>'").formatted(Formatting.WHITE));
        MessageHelper.sendSystemMessage(player, Text.literal("For more information about the various commands that exist do '/bingo'").formatted(Formatting.WHITE));

        try {
            BingoSettingsCommand.tellSettings(player);
        } catch (CommandSyntaxException e) {
            BingoMod.LOGGER.error("Unable to tell player: %s the command options".formatted(player.getDisplayName().getString()));
            e.printStackTrace();
        }

        if (!BingoPlayers.contains(player.getUuid())) {
            var scoreboard = player.getScoreboard();
            var team = player.getScoreboardTeam();
            if (BingoMod.CONFIG.AssignRandomTeamOnJoin && team == null) {
                var teams = new ArrayList<>(scoreboard.getTeams());
                var randomTeamIndex = ThreadLocalRandom.current().nextInt(0, teams.size());
                scoreboard.addPlayerToTeam(player.getName().getString(), teams.get(randomTeamIndex));
            } else if (!BingoMod.CONFIG.AssignRandomTeamOnJoin)
                player.getScoreboard().clearPlayerTeam(player.getName().getString());

            if (BingoMod.CONFIG.SpawnSettings.TeleportToHubOnJoin && player.isAlive())
                tpToBingoSpawn(player);
        } else if (Game.Status == GameStatus.Idle && BingoMod.CONFIG.SpawnSettings.TeleportToHubOnJoin && player.isAlive())
            tpToBingoSpawn(player);
    }
}
