package encrypted.dssb.gamemode;

import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.replaceblocks.ReplacementBlock;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Blackout extends GameMode {

    public Blackout(MinecraftServer server, ArrayList<Item> items) throws Exception {
        super(server);
        var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
        Card = new BingoCard(world, items);
    }

    @Override
    public void start() {
        var text = Text.literal("Game of Blackout Bingo starting!").formatted(Formatting.GREEN);
        MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), text);

        initialize();
    }

    @Override
    public void end() {
        var world = WorldHelper.getWorldByName(Server, BingoManager.GameSettings.Dimension);
        if (world != null) {
            for (var replacement : BingoMod.REPLACEMENT_BLOCKS.Blocks) {
                for (var block : Registries.BLOCK) {
                    if (block.asItem().toString().equals(replacement.DefaultBlock)) {
                        world.setBlockState(replacement.Pos.getBlockPos(), block.getDefaultState());
                        break;
                    }
                }
            }
        }

        Starting = false;
        TimerRunning = false;
    }

    @Override
    public void handleCountdown() {
        var elapsedSeconds = (System.currentTimeMillis() - CountDownStart) / 1000;

        if (CurrentCountdownSecond < elapsedSeconds) {
            CurrentCountdownSecond = elapsedSeconds;
            var text = Text.literal("%s".formatted(30 - elapsedSeconds)).formatted(Formatting.GOLD);
            MessageHelper.broadcastOverlay(Server.getPlayerManager(), text);

            if (elapsedSeconds >= 30) {
                for (var player : BingoManager.getValidPlayers(Server.getPlayerManager())) {
                    player.setMovementSpeed(1);
                    BingoManager.givePlayerEquipment(player, false);
                    BingoManager.givePlayerStatusEffects(player, false);
                }

                Starting = false;

                if (BingoManager.GameSettings.TimeLimit > 0) {
                    TimerRunning = true;
                    TimerStart = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void handleTimer() {
        var elapsedSeconds = (System.currentTimeMillis() - TimerStart) / 1000;

        if (CurrentTimerSecond < elapsedSeconds) {
            CurrentTimerSecond = elapsedSeconds;
            var remaining = BingoManager.GameSettings.TimeLimit * 60L - elapsedSeconds;

            var hours = remaining / 3600;
            var minutes = remaining / 60;
            var seconds = remaining % 60;

            var hourText = hours == 0 ? "" : hours + ":";
            var minuteText = minutes < 10 && hours > 0 ? "0" + minutes + ":" : minutes + ":";
            minuteText = minutes == 0 ? "" : minuteText;
            var secondText = seconds < 10 ? "0" + seconds : "" + seconds;
            var text = Text.literal("%s%s%s".formatted(hourText, minuteText, secondText)).formatted(Formatting.GOLD);
            MessageHelper.broadcastOverlay(Server.getPlayerManager(), text);

            if (remaining <= 0) {
                TimerRunning = false;
                handleGameTimeout();
            }
        }
    }

    private void handleGameTimeout() {
        var teams = new HashMap<AbstractTeam, Integer>();
        for (var team : Server.getScoreboard().getTeams().stream().filter(t -> t.getPlayerList().size() > 0).toList())
            teams.put(team, 0);

        for (var row : Card.slots) {
            for (var slot : row) {
                if (slot.teams.size() > 0) {
                    for (var team : slot.teams)
                        teams.put(team, teams.get(team) + 1);
                }
            }
        }

        Map.Entry<AbstractTeam, Integer> maxTeam = null;
        var tie = false;
        for (var team : teams.entrySet()) {
            if (maxTeam == null || team.getValue() > maxTeam.getValue()) {
                maxTeam = team;
                tie = false;
            } else if (team.getValue().intValue() == maxTeam.getValue().intValue())
                tie = true;
        }

        BingoManager.tpAllToBingoSpawn(Server);
        BingoManager.resetPlayers(Server);
        BingoManager.GameInProgress = false;

        if (maxTeam != null && !tie)
            handleWin(maxTeam.getKey());
        else {
            end();
            Text text = Text.literal("The game has ended in a tie.").formatted(Formatting.GOLD);
            MessageHelper.broadcastOverlay(Server.getPlayerManager(), text);
        }

        for (var player : BingoManager.getValidPlayers(Server.getPlayerManager()))
            player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 0.5f, 1);
    }

    private void handleWin(AbstractTeam team) {
        TimerRunning = false;

        final Text bingoFinished = Text.literal("%s team wins!".formatted(team.getName())).formatted(team.getColor());
        MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), bingoFinished);

        var world = WorldHelper.getWorldByName(Server, BingoMod.CONFIG.SpawnSettings.Dimension);
        if (world != null) {
            for (var replacement : BingoMod.REPLACEMENT_BLOCKS.Blocks) {
                var coloredBlock = getColoredBlock(team, replacement);
                if (coloredBlock != null)
                    world.setBlockState(replacement.Pos.getBlockPos(), coloredBlock);
            }

            for (var i = 0; i < Card.size; i++) {
                for (var j = 0; j < Card.size; j++) {
                    var slot = Card.slots[i][j];
                    var framePos = BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().offset(Direction.Axis.Y, Card.size - 1 - i).offset(Direction.EAST, j);
                    if (slot.teams.contains(team))
                        world.setBlockState(framePos, getConcrete(team));
                }
            }
        }
    }

    @Override
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

                    final Text itemFound = Text.literal("%s found item: %s".formatted(player.getDisplayName().getString(), item.getName().getString())).formatted(foundByTeam.getColor());
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

    private boolean parseCardForBingo(AbstractTeam team) {
        // check slots
        for (var row : Card.slots) {
            for (var slot : row) {
                if (!slot.teams.contains(team))
                    return false;
            }
        }
        return true;
    }

    public boolean checkBingo(AbstractTeam team) {

        if (parseCardForBingo(team)) {
            handleWin(team);
            return true;
        }
        return false;
    }

    private BlockState getColoredBlock(AbstractTeam team, ReplacementBlock replacement) {
        for (var block : Registries.BLOCK) {
            var blockName = block.asItem().toString();
            switch (team.getName()) {
                case "Red":
                    if (blockName.equals(replacement.RedBlock))
                        return block.getDefaultState();
                    break;
                case "Green":
                    if (blockName.equals(replacement.GreenBlock))
                        return block.getDefaultState();
                    break;
                case "Blue":
                    if (blockName.equals(replacement.BlueBlock))
                        return block.getDefaultState();
                    break;
                case "Purple":
                    if (blockName.equals(replacement.PurpleBlock))
                        return block.getDefaultState();
                    break;
                case "Pink":
                    if (blockName.equals(replacement.PinkBlock))
                        return block.getDefaultState();
                    break;
                case "Orange":
                    if (blockName.equals(replacement.OrangeBlock))
                        return block.getDefaultState();
                    break;
                case "Yellow":
                    if (blockName.equals(replacement.YellowBlock))
                        return block.getDefaultState();
                    break;
                case "Cyan":
                    if (blockName.equals(replacement.CyanBlock))
                        return block.getDefaultState();
                    break;
            }
        }

        return null;
    }

    private BlockState getConcrete(AbstractTeam team) {
        return switch (team.getName()) {
            case "Red" -> Blocks.RED_CONCRETE.getDefaultState();
            case "Green" -> Blocks.LIME_CONCRETE.getDefaultState();
            case "Purple" -> Blocks.PURPLE_CONCRETE.getDefaultState();
            case "Cyan" -> Blocks.CYAN_CONCRETE.getDefaultState();
            case "Pink" -> Blocks.PINK_CONCRETE.getDefaultState();
            case "Orange" -> Blocks.ORANGE_CONCRETE.getDefaultState();
            case "Blue" -> Blocks.BLUE_CONCRETE.getDefaultState();
            case "Yellow" -> Blocks.YELLOW_CONCRETE.getDefaultState();
            default -> null;
        };
    }
}
