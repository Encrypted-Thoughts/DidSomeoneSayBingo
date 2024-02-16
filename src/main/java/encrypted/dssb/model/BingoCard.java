package encrypted.dssb.model;

import encrypted.dssb.util.MapRenderHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.ArrayList;

import static encrypted.dssb.util.MapRenderHelper.nearestColor;

public class BingoCard {
    public BingoItem[][] slots;
    public int size = 5;
    public int[][] bingoPixels;
    private ItemStack map;

    public BingoCard(ServerWorld world, ArrayList<Item> items) throws Exception {
        if (items.size() < (size * size))
            throw new Exception(TranslationHelper.get("dssb.error.not_enough_items"));

        slots = new BingoItem[size][size];
        var rowIndex = 0;
        var colIndex = 0;
        for (var item : items) {
            slots[rowIndex][colIndex] = new BingoItem(item);

            colIndex++;
            if (colIndex >= size) {
                colIndex = 0;
                rowIndex++;

                if (rowIndex >= size)
                    break;
            }
        }

        resetCard(world);
    }

    public void redrawCard(World world) {
        var sideOffset = 2;
        var offsetBetweenSlots = 1;
        var slotOffset = 24;

        var rowIndex = 0;
        for (var slotRow : slots) {
            var colIndex = 0;
            for (var slot : slotRow) {
                var rowLength = slot.slotPixels.length;
                var colLength = slot.slotPixels[0].length;

                var rowStart = sideOffset + 4 + rowIndex * slotOffset + rowIndex * offsetBetweenSlots;
                var colStart = sideOffset + 4 + colIndex * slotOffset + colIndex * offsetBetweenSlots;
                for (int row = rowStart; row < rowStart + rowLength; row++)
                    System.arraycopy(slot.slotPixels[row - rowStart], 0, bingoPixels[row], colStart, colLength);

                colIndex++;
            }
            rowIndex++;
        }

        createMap(world);
    }

    public void resetCard(World world) {
        bingoPixels = new int[128][128];
        for (int i = 0; i < MapRenderHelper.getBingoCardBorder().length; i++)
            bingoPixels[i] = MapRenderHelper.getBingoCardBorder()[i].clone();

        for (BingoItem[] slot : slots) {
            for (BingoItem bingoItem : slot)
                bingoItem.teams.clear();
        }

        redrawCard(world);
    }

    public BingoItem getSlot(int rowIndex, int columnIndex) {
        if (rowIndex > size || columnIndex > size || rowIndex < 0 || columnIndex < 0)
            return null;

        return slots[rowIndex][columnIndex];
    }

    public ItemStack getMap() {
        return map.copy();
    }

    private void createMap(World world) {
        map = new ItemStack(Items.FILLED_MAP);
        var id = 1;
        var nbt = new NbtCompound();

        nbt.putString("dimension", world.getRegistryKey().getValue().toString());
        nbt.putInt("xCenter", 0);
        nbt.putInt("zCenter", 0);
        nbt.putBoolean("locked", true);
        nbt.putBoolean("unlimitedTracking", false);
        nbt.putBoolean("trackingPosition", false);
        nbt.putByte("scale", (byte) 3);
        var mapState = MapState.fromNbt(nbt);
        world.putMapState(FilledMapItem.getMapName(id), mapState);
        map.getOrCreateNbt().putInt("map", id);

        updateMapState(mapState);
    }

    public void updateMap(PlayerEntity player, int rowIndex, int colIndex, boolean lockout) {
        var sideOffset = 2;
        var offsetBetweenSlots = 1;
        var slotOffset = 24;

        var team = player.getScoreboardTeam();
        if (team == null)
            return;

        int[][] pixels = MapRenderHelper.getColorSlotArea(team, lockout);

        if (pixels != null) {
            var rowStart = sideOffset + rowIndex * slotOffset + rowIndex * offsetBetweenSlots;
            var colStart = sideOffset + colIndex * slotOffset + colIndex * offsetBetweenSlots;
            for (int row = rowStart; row < rowStart + slotOffset; row++) {
                for (int col = colStart; col < colStart + slotOffset; col++) {
                    var nearest = nearestColor(pixels[row - rowStart][col - colStart]);
                    if (nearest != 0)
                        bingoPixels[row][col] = nearest;
                }
            }

            updateMaps(player.getServer());
        }
    }

    public void updateMaps(MinecraftServer server) {
        if (server != null && map != null) {
            var mapId = FilledMapItem.getMapId(map);
            createMap(server.getOverworld());
            for (var p : server.getPlayerManager().getPlayerList()) {
                var inventory = p.getInventory();
                for (int i = 0; i < inventory.main.size(); ++i) {
                    var id = FilledMapItem.getMapId(inventory.main.get(i));
                    if (id != null && id.equals(mapId)) {
                        inventory.main.set(i, map.copy());
                    }
                }
                var id = FilledMapItem.getMapId(inventory.offHand.get(0));
                if (id != null && id.equals(mapId))
                    inventory.offHand.set(0, map.copy());
            }
        }
    }

    private void updateMapState(MapState mapState) {
        var count = 0;
        for (var row : bingoPixels) {
            for (var pixel : row) {
                mapState.colors[count] = (byte) pixel;
                count++;
            }
        }
    }
}
