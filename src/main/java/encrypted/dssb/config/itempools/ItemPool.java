package encrypted.dssb.config.itempools;

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

public class ItemPool {
    public String Name;
    public ArrayList<ItemGroup> Items = new ArrayList<>();

    public ItemPool() {}

    public ItemPool(File file) {
        ReadFromFile(file);
    }

    public void ReadFromFile(File file) {
        if (file.exists()) {
            var gson = new Gson();
            try {
                var temp = gson.fromJson(new FileReader(file), this.getClass());
                Name = temp.Name;
                Items = temp.Items;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<ItemGroup> getItemsWithAvailableAsset(ArrayList<ItemGroup> itemGroups) {
        var list = new ArrayList<ItemGroup>();
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
                list.add(new ItemGroup(possibleItems.toArray(new String[0])));
        }
        return list;
    }

    public void SaveToFile(String filename) {
        var path = FabricLoader.getInstance().getConfigDir();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var json = gson.toJson(this);
            var directory = path.resolve("bingo/itempools/").toFile();
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
