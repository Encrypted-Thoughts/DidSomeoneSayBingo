package encrypted.dssb.config.replaceblocks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import encrypted.dssb.BingoMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;

public class ReplacementBlocksConfig {
    public ArrayList<ReplacementBlock> Blocks = new ArrayList<>();

    public void addBlocks(BlockPos pos1, BlockPos pos2, String defaultBlock, String redBlock, String greenBlock, String blueBlock, String purpleBlock, String pinkBlock, String orangeBlock, String yellowBlock, String cyanBlock) {
        var minX = Math.min(pos1.getX(), pos2.getX());
        var maxX = Math.max(pos1.getX(), pos2.getX());
        var minY = Math.min(pos1.getY(), pos2.getY());
        var maxY = Math.max(pos1.getY(), pos2.getY());
        var minZ = Math.min(pos1.getZ(), pos2.getZ());
        var maxZ = Math.max(pos1.getZ(), pos2.getZ());

        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                for (var z = minZ; z <= maxZ; z++) {
                    var replacementBlock = new ReplacementBlock(
                            x, y, z,
                            defaultBlock,
                            redBlock,
                            greenBlock,
                            blueBlock,
                            purpleBlock,
                            pinkBlock,
                            orangeBlock,
                            yellowBlock,
                            cyanBlock);
                    Blocks.add(replacementBlock);
                }
            }
        }
    }

    public void ReadFromFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var file = path.resolve("bingo/replacement.json").toFile();
        if (file.exists()) {
            var gson = new Gson();
            try {
                var temp = gson.fromJson(new FileReader(file), this.getClass());
                Blocks = temp.Blocks;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            SaveToFile();
        }
    }

    public void SaveToFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var json = gson.toJson(this);
            var directory = path.resolve("bingo/").toFile();
            if (!directory.exists())
                if (!directory.mkdirs()) throw new Exception("Unable to create directory to store config files.");
            try (PrintWriter writer = new PrintWriter(directory.getPath() + "/replacement.json")) {
                writer.println(json);
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Failed to save a config file.");
            e.printStackTrace();
        }
    }
}

