package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.OverworldHardItemPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HardProfileConfig extends GameProfileConfig {
    public HardProfileConfig() {
        Name = "Overworld - Hard";
        Effects = new ArrayList<>(Arrays.asList(
            new StatusEffect("slow_falling", 20, 6, true),
            new StatusEffect("regeneration", 20, 100, true)
        ));
        StartingGear = new ArrayList<>(Arrays.asList(
            new StartingItem("minecraft:stone_pickaxe", 1, true, false),
            new StartingItem("minecraft:stone_shovel", 1, true, false),
            new StartingItem("minecraft:stone_axe", 1, true, false),
            new StartingItem("minecraft:stone_sword", 1, true, false),
            new StartingItem("minecraft:bread", 64, true, false)
        ));
        ItemPools = new ArrayList<>(List.of(
                new OverworldHardItemPool().Name
        ));
    }
}
