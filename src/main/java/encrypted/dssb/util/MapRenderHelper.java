package encrypted.dssb.util;

import encrypted.dssb.BingoMod;
import net.minecraft.block.MapColor;
import net.minecraft.scoreboard.AbstractTeam;

import javax.imageio.ImageIO;
import java.util.ArrayList;

public class MapRenderHelper {
    private static int[][] bingoCardBorder = null;

    private static int[][] redSlotArea;
    private static int[][] greenSlotArea;
    private static int[][] purpleSlotArea;
    private static int[][] cyanSlotArea;
    private static int[][] pinkSlotArea;
    private static int[][] orangeSlotArea;
    private static int[][] blueSlotArea;
    private static int[][] yellowSlotArea;

    private static int[][] redLockoutSlotArea;
    private static int[][] greenLockoutSlotArea;
    private static int[][] purpleLockoutSlotArea;
    private static int[][] cyanLockoutSlotArea;
    private static int[][] pinkLockoutSlotArea;
    private static int[][] orangeLockoutSlotArea;
    private static int[][] blueLockoutSlotArea;
    private static int[][] yellowLockoutSlotArea;

    public static int[][] getColorSlotArea(AbstractTeam team, boolean lockout){
        return switch (team.getName()) {
            case "Red" -> lockout ? redLockoutSlotArea : redSlotArea;
            case "Green" -> lockout ? greenLockoutSlotArea : greenSlotArea;
            case "Purple" -> lockout ? purpleLockoutSlotArea : purpleSlotArea;
            case "Cyan" -> lockout ? cyanLockoutSlotArea : cyanSlotArea;
            case "Pink" -> lockout ? pinkLockoutSlotArea : pinkSlotArea;
            case "Orange" -> lockout ? orangeLockoutSlotArea : orangeSlotArea;
            case "Blue" -> lockout ? blueLockoutSlotArea : blueSlotArea;
            case "Yellow" -> lockout ? yellowLockoutSlotArea : yellowSlotArea;
            default -> null;
        };
    }

    public static int[][] getBingoCardBorder() {
        if (bingoCardBorder == null)
            loadBingoCardBorder();
        return  bingoCardBorder;
    }

    public static ArrayList<MapColor> getMapColors() {
        var mapColors = new ArrayList<MapColor>();
        for (var i=0; i<64; i++)
            mapColors.add(MapColor.get(i));
        return mapColors;
    }

    private static double distance(double[] a, double[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2));
    }

    private static double[] applyShade(double[] color, int ind) {
        double brightness = MapColor.Brightness.values()[ind].brightness / 255.0;
        return new double[] { color[0] * brightness, color[1] * brightness, color[2] * brightness };
    }

    public static int nearestColor(int imageColor) {
        var colors = getMapColors();
        var imageVec = new double[3];
        imageVec[0] = ((imageColor >> 8) & 0xFF) / 255.0;
        imageVec[1] = ((imageColor >> 16) & 0xFF) / 255.0;
        imageVec[2] = (imageColor & 0xFF) / 255.0;
        var alpha = (imageColor >> 24) & 0xFF;

        int closestColor = 0;
        double lowestDistance = 10000;
        for (var color : colors) {
            var mcColor = 0xff000000 | color.color;
            var mcColorVec = new double[3];
            mcColorVec[0] = ((mcColor >> 8) & 0xFF) / 255.0;
            mcColorVec[1] = ((mcColor >> 16) & 0xFF) / 255.0;
            mcColorVec[2] = (mcColor & 0xFF) / 255.0;

            var brightnesses = MapColor.Brightness.values();
            for (var shade : brightnesses) {
                double distance = distance(imageVec, applyShade(mcColorVec, shade.id));
                if (distance < lowestDistance) {
                    lowestDistance = distance;
                    if (color.id == 0 && alpha == 255) closestColor = 119;
                    else closestColor = color.id * brightnesses.length + shade.id;
                }
            }
        }
        return closestColor;
    }

    public static void loadBingoCardBorder() {
        try {
            bingoCardBorder = new int[128][128];
            var pixels = getPixelArrayOfImage("/assets/dssb/bingo_card.png", 128, 128);

            for (int row = 0; row < pixels.length; row++) {
                for (int col = 0; col < pixels[row].length; col++) {
                    var nearest = nearestColor(pixels[row][col]);
                    if (nearest == 0) nearest = 33; // gray
                    bingoCardBorder[row][col] = nearest;
                }
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Unable to load bingo board resource.");
            BingoMod.LOGGER.error(e.getMessage());
        }
    }

    public static void loadTeamSlotAreas() {
        try {
            var height = 24;
            var width = 24;

            redSlotArea = getPixelArrayOfImage("/assets/dssb/red_overlay.png", height, width);
            greenSlotArea = getPixelArrayOfImage("/assets/dssb/green_overlay.png", height, width);
            purpleSlotArea = getPixelArrayOfImage("/assets/dssb/purple_overlay.png", height, width);
            cyanSlotArea = getPixelArrayOfImage("/assets/dssb/cyan_overlay.png", height, width);
            pinkSlotArea = getPixelArrayOfImage("/assets/dssb/pink_overlay.png", height, width);
            orangeSlotArea = getPixelArrayOfImage("/assets/dssb/orange_overlay.png", height, width);
            blueSlotArea = getPixelArrayOfImage("/assets/dssb/blue_overlay.png", height, width);
            yellowSlotArea = getPixelArrayOfImage("/assets/dssb/yellow_overlay.png", height, width);

            redLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/red_lockout_overlay.png", height, width);
            greenLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/green_lockout_overlay.png", height, width);
            purpleLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/purple_lockout_overlay.png", height, width);
            cyanLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/cyan_lockout_overlay.png", height, width);
            pinkLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/pink_lockout_overlay.png", height, width);
            orangeLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/orange_lockout_overlay.png", height, width);
            blueLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/blue_lockout_overlay.png", height, width);
            yellowLockoutSlotArea = getPixelArrayOfImage("/assets/dssb/yellow_lockout_overlay.png", height, width);

        } catch (Exception e) {
            BingoMod.LOGGER.error("Unable to load resource file for one of the team areas.");
            BingoMod.LOGGER.error(e.getMessage());
        }
    }

    public static int[][] getPixelArrayOfImage(String path, int height, int width) throws Exception {
        var stream = MapRenderHelper.class.getResourceAsStream(path);
        if (stream == null) throw new Exception("Can't obtain stream for: " + path);
        var image = ImageIO.read(stream);

        int[][] result = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++)
                result[row][col] = image.getRGB(col, row);
        }
        return result;
    }

    public static int[][] getUnknownItemIcon() throws Exception {
        var stream = MapRenderHelper.class.getResourceAsStream("/assets/dssb/items/structure_void.png");
        if (stream == null) throw new Exception("Can't obtain stream for: /assets/dssb/structure_void.png");
        var image = ImageIO.read(stream);

        int[][] result = new int[16][16];
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                var nearest = nearestColor(image.getRGB(col, row));
                if (nearest == 0)
                    nearest = 37; // gray
                result[row][col]  = nearest;
            }
        }
        return result;
    }
}
