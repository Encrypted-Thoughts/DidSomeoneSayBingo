package encrypted.dssb.model;

import encrypted.dssb.BingoMod;
import encrypted.dssb.util.MapRenderHelper;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;

import java.util.ArrayList;

import static encrypted.dssb.util.MapRenderHelper.*;

public class BingoItem {
    public Item item;
    public ArrayList<AbstractTeam> teams;
    public int[][] slotPixels = new int[16][16];

    public BingoItem(Item newItem) {
        item = newItem;
        teams = new ArrayList<>();
        initializeSlotPixels(newItem);
    }

    public void initializeSlotPixels(Item item) {
        var rowSize = slotPixels.length;
        var colSize = slotPixels[0].length;

        try {
            var pixels = MapRenderHelper.getPixelArrayOfImage("/assets/dssb/items/%s.png".formatted(Registries.ITEM.getId(item).getPath()), 16, 16);
            for (int row = 0; row < rowSize; row++) {
                for (int col = 0; col < colSize; col++) {
                    var nearest = nearestColor(pixels[row][col]);
                    if (nearest == 0)
                        nearest = 37; // gray
                    slotPixels[row][col] = nearest;
                }
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Unable to load image for %s".formatted(item.toString()));
            BingoMod.LOGGER.error(e.getMessage());
        }
    }
}
