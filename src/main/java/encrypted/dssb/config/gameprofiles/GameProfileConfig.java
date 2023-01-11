package encrypted.dssb.config.gameprofiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.model.BingoItem;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GameProfileConfig {
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
    public ArrayList<PossibleItemGroup> Items = new ArrayList<>();

    public GameProfileConfig() {
        Items = getItemsWithAvailableAsset(Items);
    }

    public GameProfileConfig(File file) {
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
                TPRandomizationRadius = temp.TPRandomizationRadius;
                Effects = temp.Effects;
                StartingGear = temp.StartingGear;
                Items = getItemsWithAvailableAsset(temp.Items);
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

    public static ArrayList<PossibleItemGroup> getItemsWithAvailableAsset(ArrayList<PossibleItemGroup> itemGroups) {
        var list = new ArrayList<PossibleItemGroup>();
        for (var itemGroup : itemGroups) {
            var possibleItems = new ArrayList<String>();
            for (var item : itemGroup.Items) {
                try {
                    var stream = BingoItem.class.getResourceAsStream("/assets/dssb/items/%s.png".formatted(item));
                    if (stream == null)
                        throw new Exception("Can't obtain stream for: /assets/bingo/items/%s.png".formatted(item));
                    ImageIO.read(stream);
                    possibleItems.add(item);
                } catch (Exception e) {
                    BingoMod.LOGGER.info("No asset for item: %s".formatted(item));
                }
            }

            if (possibleItems.size() > 0)
                list.add(new PossibleItemGroup(possibleItems.toArray(new String[0])));
        }
        return list;
    }
}

