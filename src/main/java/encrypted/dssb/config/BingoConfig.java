package encrypted.dssb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.TPRandomizationSize;
import encrypted.dssb.util.TranslationHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BingoConfig {
    public String Language = "en_us";
    public boolean TranslateCommands = false;
    public SpawnConfig SpawnSettings = new SpawnConfig();
    public String DefaultGameProfile = "Overworld - Normal";
    public Coordinates DisplayBoardCoords = new Coordinates(0, 200, 0);
    public TPRandomizationSize TPRandomizationSizes = new TPRandomizationSize();
    public Boolean AssignRandomTeamOnJoin = false;

    public ArrayList<TeamConfig> Teams = new ArrayList<>(List.of(
        new TeamConfig(1, "Red", Formatting.RED, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.RED_CONCRETE.asItem().toString()),
        new TeamConfig(2, "Orange", Formatting.GOLD, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.ORANGE_CONCRETE.asItem().toString()),
        new TeamConfig(3, "Yellow", Formatting.YELLOW, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.YELLOW_CONCRETE.asItem().toString()),
        new TeamConfig(4, "Green", Formatting.GREEN, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.LIME_CONCRETE.asItem().toString()),
        new TeamConfig(5, "Cyan", Formatting.AQUA, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.CYAN_CONCRETE.asItem().toString()),
        new TeamConfig(6, "Blue", Formatting.BLUE, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.BLUE_CONCRETE.asItem().toString()),
        new TeamConfig(7, "Purple", Formatting.DARK_PURPLE, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.PURPLE_CONCRETE.asItem().toString()),
        new TeamConfig(8, "Pink", Formatting.LIGHT_PURPLE, AbstractTeam.CollisionRule.PUSH_OWN_TEAM, false, Blocks.PINK_CONCRETE.asItem().toString())
    ));

    public ArrayList<String> BingoDimensions = new ArrayList<>(List.of(
        "minecraft:overworld",
        "minecraft:the_nether"
    ));

    public void readFromFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var file = path.resolve("bingo/bingo.json").toFile();
        if (file.exists()) {
            var gson = new Gson();
            try {
                var temp = gson.fromJson(new FileReader(file), this.getClass());
                Language = temp.Language;
                TranslateCommands = temp.TranslateCommands;
                SpawnSettings = temp.SpawnSettings;
                DefaultGameProfile = temp.DefaultGameProfile;
                DisplayBoardCoords = temp.DisplayBoardCoords;
                TPRandomizationSizes = temp.TPRandomizationSizes;
                AssignRandomTeamOnJoin = temp.AssignRandomTeamOnJoin;
                BingoDimensions = temp.BingoDimensions;
                Teams = temp.Teams;
            } catch (FileNotFoundException e) {
                BingoMod.LOGGER.error("Failed to read a config file.");
                BingoMod.LOGGER.error(e.getMessage());
            }
        } else
            saveToFile();
    }

    public void saveToFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var json = gson.toJson(this);
            var directory = path.resolve("bingo/").toFile();
            if (!directory.exists())
                if (!directory.mkdirs()) throw new Exception(TranslationHelper.get("Unable to create directory to store config files."));
            try (PrintWriter writer = new PrintWriter(directory.getPath() + "/bingo.json")) {
                writer.println(json);
            }
        } catch (Exception e) {
            BingoMod.LOGGER.error("Failed to save a config file.");
            BingoMod.LOGGER.error(e.getMessage());
        }
    }
}

