package encrypted.dssb.model;

import encrypted.dssb.util.MapRenderHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import java.util.ArrayList;

import static encrypted.dssb.util.MapRenderHelper.nearestColor;

public class BingoCard {
    public BingoItem[][] slots;
    public int size = 5;
    public int[][] bingoPixels;
    private ItemStack map;

    public BingoCard(ServerLevel world, ArrayList<Item> items) throws Exception {
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

    public void redrawCard(ServerLevel world) {
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

    public void resetCard(ServerLevel world) {
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

    private void createMap(ServerLevel world) {
        map = new ItemStack(Items.FILLED_MAP);
        var mapState = MapItemSavedData.createForClient((byte) 3, true, world.dimension());
        var mapIdComponent = new MapId(1);
        world.setMapData(mapIdComponent, mapState);
        map.set(DataComponents.MAP_ID, mapIdComponent);
        updateMapState(mapState);
    }

    public void updateMap(Player player, int rowIndex, int colIndex, boolean lockout) {
        var sideOffset = 2;
        var offsetBetweenSlots = 1;
        var slotOffset = 24;

        var team = player.getTeam();
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

            updateMaps(player.level().getServer());
        }
    }

    public void updateMaps(MinecraftServer server) {
        if (server != null && map != null) {
            createMap(server.overworld());
            var mapId = map.get(DataComponents.MAP_ID);
            if (mapId != null) {
                for (var p : server.getPlayerList().getPlayers()) {
                    var inventory = p.getInventory();
                    for (int i = 0; i < inventory.getContainerSize(); ++i) {
                        var id = inventory.getItem(i).get(DataComponents.MAP_ID);
                        if (id != null && id.id() == mapId.id())
                            inventory.setItem(i, map.copy());
                    }
                }
            }
        }
    }

    private void updateMapState(MapItemSavedData mapState) {
        var count = 0;
        for (var row : bingoPixels) {
            for (var pixel : row) {
                mapState.colors[count] = (byte) pixel;
                count++;
            }
        }
    }
}
