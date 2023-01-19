package encrypted.dssb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.TPRandomizationSize;
import encrypted.dssb.config.replaceblocks.Coordinates;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BingoConfig {
    public SpawnConfig SpawnSettings = new SpawnConfig();
    public String DefaultGameProfile = "Overworld - Normal";
    public Coordinates DisplayBoardCoords = new Coordinates(0, 200, 0);
    public TPRandomizationSize TPRandomizationSizes = new TPRandomizationSize();
    public Boolean AssignRandomTeamOnJoin = false;

    public ArrayList<String> BingoDimensions = new ArrayList<>(List.of(
            "minecraft:overworld",
            "minecraft:the_nether"
    ));

    public void ReadFromFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var file = path.resolve("bingo/bingo.json").toFile();
        if (file.exists()) {
            var gson = new Gson();
            try {
                var temp = gson.fromJson(new FileReader(file), this.getClass());
                SpawnSettings = temp.SpawnSettings;
                DefaultGameProfile = temp.DefaultGameProfile;
                DisplayBoardCoords = temp.DisplayBoardCoords;
                TPRandomizationSizes = temp.TPRandomizationSizes;
                AssignRandomTeamOnJoin = temp.AssignRandomTeamOnJoin;
                BingoDimensions = temp.BingoDimensions;
            } catch (FileNotFoundException e) {
                BingoMod.LOGGER.error("Failed to read a config file.");
                e.printStackTrace();
            }
        } else
            SaveToFile();
    }

    public void SaveToFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var json = gson.toJson(this);
            var directory = path.resolve("bingo/").toFile();
            if (!directory.exists())
                if (!directory.mkdirs()) throw new Exception("Unable to create directory to store config files.");
            try (PrintWriter writer = new PrintWriter(directory.getPath() + "/bingo.json")) {
                writer.println(json);
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Failed to save a config file.");
            e.printStackTrace();
        }
    }
}

