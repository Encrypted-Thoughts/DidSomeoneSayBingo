package encrypted.dssb.gamemode;

import encrypted.dssb.model.BingoCard;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.WorldHelper;
import encrypted.dssb.BingoManager;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Bingo extends GameModeBase {

    public Bingo(MinecraftServer server, ArrayList<Item> items) throws Exception {
        super(server);
        var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
        Card = new BingoCard(world, items);
        Name = "Bingo";
    }

    @Override
    public void start() {
        Status = GameStatus.Loading;
        var text = Text.literal("Game of Normal Bingo starting!").formatted(Formatting.GREEN);
        MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), text);

        initialize();
    }

    private void handleGameTimeout() {
        var teams = new HashMap<AbstractTeam, Integer>();
        for (var team : Server.getScoreboard().getTeams().stream().filter(t -> !t.getPlayerList().isEmpty()).toList())
            teams.put(team, 0);

        for (var row : Card.slots) {
            for (var slot : row) {
                if (!slot.teams.isEmpty()) {
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

    @Override
    public void end() {
        setScoreboardStats(0);
        Status = GameStatus.Idle;
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
                    givePlayerEquipment(player, false);
                    givePlayerStatusEffects(player, false);
                }

                Status = GameStatus.Playing;

                if (BingoManager.GameSettings.TimeLimit > 0) {
                    TimerRunning = true;
                    TimerStart = System.currentTimeMillis();
                    CurrentTimerSecond = 0;
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
            var secondText = seconds < 10 ? "0" + seconds : seconds;
            var text = Text.literal("%s%s%s".formatted(hourText, minuteText, secondText)).formatted(Formatting.GOLD);
            MessageHelper.broadcastOverlay(Server.getPlayerManager(), text);

            if (remaining <= 0) {
                TimerRunning = false;
                handleGameTimeout();
            }
        }
    }

    private boolean parseCardForBingo(AbstractTeam team) {
        // check slots
        for (var row : Card.slots) {
            var bingo = true;

            for (var col : row) {
                if (!col.teams.contains(team)) {
                    bingo = false;
                    break;
                }
            }

            if (bingo)
                return true;
        }

        // check columns
        for (var i = 0; i < Card.size; i++) {
            var bingo = true;

            for (var row : Card.slots) {
                if (!row[i].teams.contains(team)) {
                    bingo = false;
                    break;
                }
            }

            if (bingo)
                return true;
        }

        //check diagonals
        var bingo = true;
        for (var i = 0; i < Card.size; i++) {
            if (!Card.slots[i][i].teams.contains(team)) {
                bingo = false;
                break;
            }
        }
        if (bingo)
            return true;

        bingo = true;
        for (var i = 0; i < Card.size; i++) {
            if (!Card.slots[i][Card.size - 1 - i].teams.contains(team)) {
                bingo = false;
                break;
            }
        }
        return bingo;
    }

    public boolean checkBingo(AbstractTeam team) {

        if (parseCardForBingo(team)) {
            handleWin(team);
            return true;
        }
        return false;
    }
}
