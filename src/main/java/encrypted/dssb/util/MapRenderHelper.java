package encrypted.dssb.util;

import encrypted.dssb.BingoMod;
import net.minecraft.block.MapColor;
import net.minecraft.scoreboard.AbstractTeam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MapRenderHelper {
    private static final double[] shadeCoeffs = { 0.71, 0.86, 1.0, 0.53 };

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

    private static double distance(double[] vectorA, double[] vectorB) {
        return Math.sqrt(Math.pow(vectorA[0] - vectorB[0], 2) + Math.pow(vectorA[1] - vectorB[1], 2) + Math.pow(vectorA[2] - vectorB[2], 2));
    }

    private static double[] applyShade(double[] color, int ind) {
        double coeff = shadeCoeffs[ind];
        return new double[] { color[0] * coeff, color[1] * coeff, color[2] * coeff };
    }

    public static int nearestColor(Color imageColor) {
        var colors = getMapColors();
        double[] imageVec = {
                (double) imageColor.getRed() / 255.0,
                (double) imageColor.getGreen() / 255.0,
                (double) imageColor.getBlue() / 255.0
        };
        int best_color = 0;
        double lowest_distance = 10000;
        for (int k = 0; k < colors.size(); k++) {
            Color mcColor = new Color(colors.get(k).color);
            double[] mcColorVec = {
                    (double) mcColor.getRed() / 255.0,
                    (double) mcColor.getGreen() / 255.0,
                    (double) mcColor.getBlue() / 255.0
            };
            for (int shadeInd = 0; shadeInd < shadeCoeffs.length; shadeInd++) {
                double distance = distance(imageVec, applyShade(mcColorVec, shadeInd));
                if (distance < lowest_distance) {
                    lowest_distance = distance;
                    if (k == 0 && imageColor.getAlpha() == 255)
                        best_color = 119;
                    else
                        best_color = k * shadeCoeffs.length + shadeInd;
                }
            }
        }
        return best_color;
    }

    public static int[][] convertPixelArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        var newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static void loadBingoCardBorder() {
        try {
            bingoCardBorder = new int[128][128];
            var pixels = getPixelArrayOfImage("/assets/dssb/bingo_card.png", 128, 128);

            for (int row = 0; row < pixels.length; row++) {
                for (int col = 0; col < pixels[row].length; col++) {
                    var imageColor = new Color(pixels[row][col], true);
                    var nearest = nearestColor(imageColor);
                    if (nearest == 0)
                        nearest = 33; // gray
                    bingoCardBorder[row][col] = nearest;
                }
            }
        } catch (Exception ex) {
            BingoMod.LOGGER.warn("Unable to load bingo board resource.");
            ex.printStackTrace();
        }
    }

    public static void loadTeamSlotAreas() {
        try {
            var height = 22;
            var width = 22;

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
            e.printStackTrace();
        }
    }

    public static int[][] getPixelArrayOfImage(String path, int height, int width) throws Exception {
        var stream = MapRenderHelper.class.getResourceAsStream(path);
        if (stream == null) throw new Exception("Can't obtain stream for: " + path);
        var team = ImageIO.read(stream);
        var image = team.getScaledInstance(width, height, Image.SCALE_FAST);
        var resized = convertToBufferedImage(image);
        return convertPixelArray(resized);
    }
}
