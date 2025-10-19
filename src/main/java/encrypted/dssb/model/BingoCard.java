package encrypted.dssb.model;

import encrypted.dssb.util.MapRenderHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;

import static encrypted.dssb.util.MapRenderHelper.nearestColor;

public class BingoCard {
    public BingoItem[][] slots;
    public int size = 5;
    public int[][] bingoPixels;
    private ItemStack map;

    public BingoCard(ServerWorld world, ArrayList<Item> items) throws Exception {
        if (items.size() < (size * size))
            throw new Exception("Not enough possible items in item pools to generate a bingo card.");

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

    public void redrawCard(ServerWorld world) {
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

    public void resetCard(ServerWorld world) {
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

    private void createMap(ServerWorld world) {
        map = new ItemStack(Items.FILLED_MAP);
        var mapState = MapState.of((byte) 3, true, world.getRegistryKey());
        var mapIdComponent = new MapIdComponent(1);
        world.putMapState(mapIdComponent, mapState);
        map.set(DataComponentTypes.MAP_ID, mapIdComponent);
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

            updateMaps(player.getEntityWorld().getServer());
        }
    }

    public void updateMaps(MinecraftServer server) {
        if (server != null && map != null) {
            createMap(server.getOverworld());
            var mapId = map.get(DataComponentTypes.MAP_ID);
            if (mapId != null) {
                for (var p : server.getPlayerManager().getPlayerList()) {
                    var inventory = p.getInventory();
                    for (int i = 0; i < inventory.size(); ++i) {
                        var id = inventory.getStack(i).get(DataComponentTypes.MAP_ID);
                        if (id != null && id.id() == mapId.id())
                            inventory.setStack(i, map.copy());
                    }
                }
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
