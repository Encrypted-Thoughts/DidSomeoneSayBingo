package encrypted.dssb.gamemode;

import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoCard;
import encrypted.dssb.util.MapRenderHelper;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.WorldHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class HiddenBingo extends GameModeBase {

    public enum HiddenType {Diagonal, DoubleDiagonal, All}

    private long unlocked = 0;
    private ArrayList<int[]> lockedSlots;
    private final int unlockInterval;
    private final HiddenType hiddenType;

    public HiddenBingo(MinecraftServer server, ArrayList<Item> items, HiddenType type, int interval) throws Exception {
        super(server);
        var world = WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension);
        Card = new BingoCard(world, items);
        Name = "Hidden";

        unlockInterval = interval;
        hiddenType = type;

        hideSlots(world, type);
    }

    private void hideSlots(ServerWorld world, HiddenType type) throws Exception {
        lockedSlots = new ArrayList<>();
        for (var row = 0; row < Card.slots.length; row++) {
            for (var col = 0; col < Card.slots.length; col++) {
                switch (type) {
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
        var text = Text.literal("Game of Hidden Bingo starting!").formatted(Formatting.GREEN);
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

        final Text bingoFinished = Text.literal("%s team wins in %s!".formatted(team.getName(), readableTime)).formatted(team.getColor());
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

    private void unHideBoard(ServerWorld world) {
        var frames = world.getEntitiesByType(EntityType.GLOW_ITEM_FRAME, (glowItemFrame) -> BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().isWithinDistance(glowItemFrame.getBlockPos(), 20));
        for (var frame : frames)
            frame.remove(Entity.RemovalReason.DISCARDED);
        for (var i = 0; i < Card.size; i++) {
            for (var j = 0; j < Card.size; j++) {
                var slot = Card.slots[i][j];
                var framePos = BingoMod.CONFIG.DisplayBoardCoords.getBlockPos().offset(Direction.Axis.Y, Card.size - 1 - i).offset(Direction.EAST, j);
                var frame = new GlowItemFrameEntity(world, framePos.offset(Direction.SOUTH, 1), Direction.SOUTH);
                frame.setInvulnerable(true);
                frame.setHeldItemStack(new ItemStack(slot.item, 1), true);
                world.spawnEntity(frame);
            }
        }
    }

    private void unHideMap() {
        for (var lockedSlot : lockedSlots) {
            var slot = Card.getSlot(lockedSlot[0], lockedSlot[1]);
            slot.initializeSlotPixels(slot.item);
        }
        Card.redrawCard(Server.getOverworld());
        lockedSlots = new ArrayList<>();
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
            var secondText = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
            var text = Text.literal("%s%s%s".formatted(hourText, minuteText, secondText)).formatted(Formatting.GOLD);
            MessageHelper.broadcastOverlay(Server.getPlayerManager(), text);

            if (elapsedSeconds / unlockInterval > unlocked && !lockedSlots.isEmpty()) {
                var index = new Random().nextInt(lockedSlots.size());
                var lockedSlot = lockedSlots.remove(index);
                var slot = Card.getSlot(lockedSlot[0], lockedSlot[1]);
                slot.initializeSlotPixels(slot.item);
                Card.redrawCard(Server.getOverworld());
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
        for (var player : BingoManager.getValidPlayers(server.getPlayerManager()))
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.MASTER, 1, 0.5F);
    }

    @Override
    public boolean checkItem(Item item, PlayerEntity player) {
        var foundByTeam = player.getScoreboardTeam();
        var server = player.getServer();
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

                    var itemFound = Text.literal("%s found item: %s".formatted(player.getDisplayName().getString(), item.getName().getString())).formatted(foundByTeam.getColor());

                    int finalRow = row;
                    int finalCol = col;
                    if (lockedSlots.stream().anyMatch(l -> l[0] == finalRow && l[1] == finalCol))
                        itemFound = Text.literal(("%s found hidden item at %s, %s!".formatted(player.getDisplayName().getString(), row+1, col+1))).formatted(foundByTeam.getColor());

                    MessageHelper.broadcastChatToPlayers(Server.getPlayerManager(), itemFound);
                    playNotificationSound(player.getWorld());
                    return true;
                }
            }
        }
        return false;
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

                switch (hiddenType) {
                    case Diagonal -> {
                        if (i + j == 4) frame.setHeldItemStack(Items.STRUCTURE_VOID.getDefaultStack(), true);
                        else frame.setHeldItemStack(new ItemStack(slot.item, 1), true);
                    }
                    case DoubleDiagonal -> {
                        if (i == j || i + j == 4) frame.setHeldItemStack(Items.STRUCTURE_VOID.getDefaultStack(), true);
                        else frame.setHeldItemStack(new ItemStack(slot.item, 1), true);
                    }
                    case All -> frame.setHeldItemStack(Items.STRUCTURE_VOID.getDefaultStack(), true);
                }
                world.setBlockState(framePos, Blocks.BLACK_CONCRETE.getDefaultState());
                world.spawnEntity(frame);
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

    public void clarify(ServerPlayerEntity player, int rowIndex, int columnIndex) {
        Text text;
        if (lockedSlots.stream().anyMatch(l -> l[0] == rowIndex && l[1] == columnIndex))
            text = Text.literal("Item at position %s, %s is currently hidden".formatted(rowIndex + 1, columnIndex + 1)).formatted(Formatting.RED);
        else {
            var item = getSlot(rowIndex, columnIndex);
            if (item == null) text = Text.literal("Unable to locate item at position %s, %s".formatted(rowIndex + 1, columnIndex + 1)).formatted(Formatting.RED);
            else text = Text.literal("Item at position %s, %s: %s".formatted(rowIndex + 1, columnIndex + 1, item.item.getName().getString())).formatted(Formatting.GOLD);
        }
        if (player != null) player.sendMessage(text);
    }
}
