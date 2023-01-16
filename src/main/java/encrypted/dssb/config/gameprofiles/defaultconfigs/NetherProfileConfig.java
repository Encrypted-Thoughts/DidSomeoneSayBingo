package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.NetherItemPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetherProfileConfig extends GameProfileConfig {
    public NetherProfileConfig() {
        Name = "Nether";
        Dimension = "minecraft:the_nether";
        YSpawnOffset = 0;
        MaxYLevel = 110;
        Effects = new ArrayList<>(Arrays.asList(
                new StatusEffect("fire_resistance", 20, 100, true),
                new StatusEffect("slow_falling", 20, 6, true),
                new StatusEffect("regeneration", 20, 100, true)
        ));
        StartingGear = new ArrayList<>(Arrays.asList(
                new StartingItem("minecraft:diamond_pickaxe", 1, true, false),
                new StartingItem("minecraft:diamond_shovel", 1, true, false),
                new StartingItem("minecraft:diamond_axe", 1, true, false),
                new StartingItem("minecraft:diamond_sword", 1, true, false),
                new StartingItem("minecraft:bread", 64, true, false)
        ));
        ItemPools = new ArrayList<>(List.of(
                new NetherItemPool().Name
        ));
    }
}
