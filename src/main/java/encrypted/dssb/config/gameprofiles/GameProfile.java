package encrypted.dssb.config.gameprofiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import encrypted.dssb.BingoMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GameProfile {
    public String Name;
    public String Dimension = "minecraft:overworld";
    public String GameMode = "Bingo";
    public int YSpawnOffset = 50;
    public int MaxYLevel = 200;
    public int PlayAreaRadius = 1000000;
    public int TimeLimit = 20;
    public int TPRandomizationRadius = 2000;
    public ArrayList<StatusEffect> Effects = new ArrayList<>();
    public ArrayList<StartingItem> StartingGear = new ArrayList<>();
    public ArrayList<String> ItemPools = new ArrayList<>();

    public GameProfile() {}

    public GameProfile(GameProfile copy) {
        Name = copy.Name;
        Dimension = copy.Dimension;
        GameMode = copy.GameMode;
        YSpawnOffset = copy.YSpawnOffset;
        MaxYLevel = copy.MaxYLevel;
        PlayAreaRadius = copy.PlayAreaRadius;
        TimeLimit = copy.TimeLimit;
        TPRandomizationRadius = copy.TPRandomizationRadius;
        Effects = copy.Effects;
        StartingGear = copy.StartingGear;
        ItemPools = copy.ItemPools;
    }

    public GameProfile(File file) {
        ReadFromFile(file);
    }

    public void ReadFromFile(File file) {
        if (file.exists()) {
            var gson = new Gson();
            try {
                var temp = gson.fromJson(new FileReader(file), this.getClass());
                Name = temp.Name;
                Dimension = temp.Dimension;
                GameMode = temp.GameMode;
                YSpawnOffset = temp.YSpawnOffset;
                MaxYLevel = temp.MaxYLevel;
                PlayAreaRadius = temp.PlayAreaRadius;
                TimeLimit = temp.TimeLimit;
                TPRandomizationRadius = temp.TPRandomizationRadius;
                Effects = temp.Effects;
                StartingGear = temp.StartingGear;
                ItemPools = temp.ItemPools;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void SaveToFile(String filename) {
        var path = FabricLoader.getInstance().getConfigDir();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var json = gson.toJson(this);
            var directory = path.resolve("bingo/profiles/").toFile();
            if (!directory.exists())
                if (!directory.mkdirs()) throw new Exception("Unable to create directory to store config files.");
            try (PrintWriter writer = new PrintWriter(directory.getPath() + "/%s.json".formatted(filename))) {
                writer.println(json);
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Failed to save a config file.");
            e.printStackTrace();
        }
    }
}

