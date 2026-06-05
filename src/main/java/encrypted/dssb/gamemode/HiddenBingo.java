package encrypted.dssb.gamemode;

import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.Team;
import java.util.*;

public class HiddenBingo extends GameModeBase {

    public enum HiddenType {CheckerBoard, InverseCheckerBoard, Diagonal, DoubleDiagonal, All}

    private long unlocked = 0;
    private ArrayList<int[]> lockedSlots;
    private int unlockInterval;
    private HiddenType hiddenType = HiddenType.DoubleDiagonal;

    public HiddenBingo(MinecraftServer server, ArrayList<Item> items, String type) throws Exception {
        super(server);
        var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
        Card = new BingoCard(world, items);
        Name = "Hidden";

        var secondsOffset = 60;
        switch (type) {
            case "diagonal" -> {
                hiddenType = HiddenType.Diagonal;
                unlockInterval = (BingoManager.GameSettings.TimeLimit * 60 - secondsOffset) / 5;
            }
            case "doubleDiagonal" -> {
                hiddenType = HiddenType.DoubleDiagonal;
                unlockInterval = (BingoManager.GameSettings.TimeLimit * 60 - secondsOffset) / 9;
            }
            case "checkerboard" -> {
                hiddenType = HiddenType.CheckerBoard;
                unlockInterval = (BingoManager.GameSettings.TimeLimit * 60 - secondsOffset) / 13;
            }
            case "inverseCheckerboard" -> {
                hiddenType = HiddenType.InverseCheckerBoard;
                unlockInterval = (BingoManager.GameSettings.TimeLimit * 60 - secondsOffset) / 12;
            }
            case "all" -> {
                hiddenType = HiddenType.All;
                unlockInterval = (BingoManager.GameSettings.TimeLimit * 60 - secondsOffset) / 25;
            }
        }

        hideSlots(world, hiddenType);
    }

    private void hideSlots(ServerLevel world, HiddenType type) throws Exception {
        lockedSlots = new ArrayList<>();
        for (var row = 0; row < Card.slots.length; row++) {
            for (var col = 0; col < Card.slots.length; col++) {
                switch (type) {
                    case CheckerBoard -> {
                        var evenRow = (row + 1 & 1) == 0;
                        var evenColumn = (col + 1 & 1) == 0;
                        if (evenRow == evenColumn) {
                            lockedSlots.add(new int[] {row, col});
                            Card.slots[row][col].slotPixels = MapRenderHelper.getUnknownItemIcon();
                        }
                    }
                    case InverseCheckerBoard -> {
                        var evenRow = (row + 1 & 1) == 0;
                        var evenColumn = (col + 1 & 1) == 0;
                        if (evenRow != evenColumn) {
                            lockedSlots.add(new int[] {row, col});
                            Card.slots[row][col].slotPixels = MapRenderHelper.getUnknownItemIcon();
                        }
                    }
                    case Diagonal -> {
                        if (row + col == 4) {
                            lockedSlots.add(new int[] {row, col});
                            Card.slots[row][col].slotPixels = MapRenderHelper.getUnknownItemIcon();
                        }
                    }
                    case DoubleDiagonal -> {
                        if (row == col || row + col == 4) {
                            lockedSlots.add(new int[] {row, col});
                            Card.slots[row][col].slotPixels = MapRenderHelper.getUnknownItemIcon();
                        }
                    }
                    case All -> {
                        lockedSlots.add(new int[] {row, col});
                        Card.slots[row][col].slotPixels = MapRenderHelper.getUnknownItemIcon();
                    }
                }
            }
        }
        Card.redrawCard(world);
    }

    @Override
    public void start() {
        Status = GameStatus.Loading;
        var text = TranslationHelper.getAsText("dssb.game.hidden.starting");
        MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), text);

        initialize();
    }

    private void handleGameTimeout() {
        var teams = new HashMap<Team, Integer>();
        for (var team : Server.getScoreboard().getPlayerTeams().stream().filter(t -> !t.getPlayers().isEmpty()).toList())
            teams.put(team, 0);

        for (var row : Card.slots) {
            for (var slot : row) {
                if (!slot.teams.isEmpty()) {
                    for (var team : slot.teams)
                        teams.put(team, teams.get(team) + 1);
                }
            }
        }

        Map.Entry<Team, Integer> maxTeam = null;
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
            var text = TranslationHelper.getAsText("dssb.game.tie");
            MessageHelper.broadcastOverlay(Server.getPlayerList(), text);
        }

        for (var player : BingoManager.getValidPlayers(Server.getPlayerList()))
            player.playSound(SoundEvents.ENDER_DRAGON_DEATH, 0.5f, 1);
    }

    @Override
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

        final var bingoFinished = TranslationHelper.getAsText("dssb.game.team_wins", team.getName(), readableTime).withStyle(team.getColor());
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

            unHideBoard(world);
            unHideMap();
        }
        setScoreboardStats(getTeamNumber(team));

        Status = GameStatus.Idle;
    }

    @Override
    public void end() {
        var world = WorldHelper.getWorldByName(Server, BingoMod.CONFIG.SpawnSettings.Dimension);
        if (world != null) {
            unHideBoard(world);
            unHideMap();
        }

        setScoreboardStats(0);
        Status = GameStatus.Idle;
        TimerRunning = false;
    }

    private void unHideBoard(ServerLevel world) {
        var frames = world.getEntities(EntityType.GLOW_ITEM_FRAME, (glowItemFrame) -> BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().closerThan(glowItemFrame.blockPosition(), 20));
        for (var frame : frames)
            frame.remove(Entity.RemovalReason.DISCARDED);
        for (var i = 0; i < Card.size; i++) {
            for (var j = 0; j < Card.size; j++) {
                var slot = Card.slots[i][j];
                var framePos = BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().relative(Direction.Axis.Y, Card.size - 1 - i).relative(Direction.EAST, j);
                var frame = new GlowItemFrame(world, framePos.relative(Direction.SOUTH, 1), Direction.SOUTH);
                frame.setInvulnerable(true);
                frame.setItem(new ItemStack(slot.item, 1), true);
                world.addFreshEntity(frame);
            }
        }
    }

    private void unHideMap() {
        for (var lockedSlot : lockedSlots) {
            var slot = Card.getSlot(lockedSlot[0], lockedSlot[1]);
            slot.initializeSlotPixels(slot.item);
        }
        Card.redrawCard(Server.overworld());
        lockedSlots = new ArrayList<>();
    }

    @Override
    public void handleCountdown() {
        var elapsedSeconds = (System.currentTimeMillis() - CountDownStart) / 1000;

        if (CurrentCountdownSecond < elapsedSeconds) {
            CurrentCountdownSecond = elapsedSeconds;
            var text = TranslationHelper.getAsText("dssb.game.countdown",30 - elapsedSeconds);
            MessageHelper.broadcastOverlay(Server.getPlayerList(), text);

            if (elapsedSeconds >= 30) {
                for (var player : BingoManager.getValidPlayers(Server.getPlayerList())) {
                    player.setSpeed(1);
                    player.setNoGravity(false);
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
            var secondText = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
            var text = TranslationHelper.getAsText("dssb.game.timer", hourText, minuteText, secondText);
            MessageHelper.broadcastOverlay(Server.getPlayerList(), text);

            if (elapsedSeconds / unlockInterval > unlocked && !lockedSlots.isEmpty()) {
                var index = new Random().nextInt(lockedSlots.size());
                var lockedSlot = lockedSlots.remove(index);
                var slot = Card.getSlot(lockedSlot[0], lockedSlot[1]);
                slot.initializeSlotPixels(slot.item);
                Card.redrawCard(Server.overworld());
                playUnlockSound(Server);
                unlocked++;
            }

            if (remaining <= 0) {
                TimerRunning = false;
                handleGameTimeout();
            }
        }
    }

    public void playUnlockSound(MinecraftServer server) {
        for (var player : BingoManager.getValidPlayers(server.getPlayerList()))
            player.level().playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.MASTER, 1, 0.5F);
    }

    @Override
    public boolean checkItem(Item item, Player player) {
        var foundByTeam = player.getTeam();
        var server = player.level().getServer();
        if (foundByTeam == null || server == null)
            return false;

        for (var row = 0; row < Card.slots.length; row++) {
            for (var col = 0; col < Card.slots.length; col++) {
                var bingoItem = Card.slots[row][col];
                if (bingoItem.item == item) {
                    for (var team : bingoItem.teams)
                        if (team == foundByTeam) return false;

                    bingoItem.teams.add(foundByTeam);
                    Card.updateMap(player, row, col, false);

                    var itemFound = TranslationHelper.getAsText("dssb.game.item_found", PlayerHelper.getPlayerName(player), item.getName().getString()).withStyle(foundByTeam.getColor());

                    int finalRow = row;
                    int finalCol = col;
                    if (lockedSlots.stream().anyMatch(l -> l[0] == finalRow && l[1] == finalCol))
                        itemFound = TranslationHelper.getAsText("dssb.game.hidden.item_found", PlayerHelper.getPlayerName(player), row+1, col+1).withStyle(foundByTeam.getColor());

                    MessageHelper.broadcastChatToPlayers(Server.getPlayerList(), itemFound);
                    playNotificationSound(player.level());
                    return true;
                }
            }
        }
        return false;
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

                switch (hiddenType) {
                    case CheckerBoard -> {
                        var evenRow = (i + 1 & 1) == 0;
                        var evenColumn = (j + 1 & 1) == 0;
                        if (evenRow == evenColumn)
                            frame.setItem(Items.STRUCTURE_VOID.getDefaultInstance(), true);
                        else
                            frame.setItem(new ItemStack(slot.item, 1), true);
                    }
                    case InverseCheckerBoard -> {
                        var evenRow = (i + 1 & 1) == 0;
                        var evenColumn = (j + 1 & 1) == 0;
                        if (evenRow != evenColumn)
                            frame.setItem(Items.STRUCTURE_VOID.getDefaultInstance(), true);
                        else
                            frame.setItem(new ItemStack(slot.item, 1), true);
                    }
                    case Diagonal -> {
                        if (i + j == 4) frame.setItem(Items.STRUCTURE_VOID.getDefaultInstance(), true);
                        else frame.setItem(new ItemStack(slot.item, 1), true);
                    }
                    case DoubleDiagonal -> {
                        if (i == j || i + j == 4) frame.setItem(Items.STRUCTURE_VOID.getDefaultInstance(), true);
                        else frame.setItem(new ItemStack(slot.item, 1), true);
                    }
                    case All -> frame.setItem(Items.STRUCTURE_VOID.getDefaultInstance(), true);
                }
                world.setBlockAndUpdate(framePos, Blocks.BLACK_CONCRETE.defaultBlockState());
                world.addFreshEntity(frame);
            }
        }
    }

    private boolean parseCardForBingo(Team team) {
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

    public boolean checkBingo(Team team) {

        if (parseCardForBingo(team)) {
            handleWin(team);
            return true;
        }
        return false;
    }

    public void clarify(ServerPlayer player, int rowIndex, int columnIndex) {
        Component text;
        if (lockedSlots.stream().anyMatch(l -> l[0] == rowIndex && l[1] == columnIndex))
            text = TranslationHelper.getAsText("dssb.game.hidden.item_hidden", rowIndex + 1, columnIndex + 1);
        else {
            var item = getSlot(rowIndex, columnIndex);
            if (item == null) text = TranslationHelper.getAsText("dssb.error.clarify_fail",rowIndex + 1, columnIndex + 1);
            else text = TranslationHelper.getAsText("dssb.game.clarify", rowIndex + 1, columnIndex + 1, item.item.getName().getString());
        }
        if (player != null) player.sendSystemMessage(text);
    }
}
