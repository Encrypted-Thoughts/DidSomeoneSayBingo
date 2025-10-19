package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.NetherItemPool;

import java.util.ArrayList;
import java.util.List;

public class NetherPreset extends GamePreset {
    public NetherPreset() {
        Name = "Nether";
        Dimension = "minecraft:the_nether";
        YSpawnOffset = 0;
        MaxYLevel = 110;
        Effects = new ArrayList<>(List.of(
                new StatusEffect("minecraft:fire_resistance", 20, 100, true),
                new StatusEffect("minecraft:slow_falling", 20, 6, true),
                new StatusEffect("minecraft:regeneration", 20, 100, true)
        ));
        StartingGear = new ArrayList<>(List.of(
                new StartingItem("minecraft:diamond_pickaxe", 1, true, false),
                new StartingItem("minecraft:diamond_shovel", 1, true, false),
                new StartingItem("minecraft:diamond_axe", 1, true, false),
                new StartingItem("minecraft:diamond_sword", 1, true, false),
                new StartingItem("minecraft:bread", 64, true, false),
                new StartingItem("minecraft:crafting_table", 64, true, false)
        ));
        ItemPools = new ArrayList<>(List.of(
                new NetherItemPool().Name
        ));
    }
}
