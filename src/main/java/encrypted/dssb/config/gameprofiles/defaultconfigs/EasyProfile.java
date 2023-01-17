package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GameProfile;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.OverworldEasyItemPool;

import java.util.ArrayList;
import java.util.List;

public class EasyProfile extends GameProfile {
    public EasyProfile() {
        Name = "Overworld - Easy";
        Effects = new ArrayList<>(List.of(
            new StatusEffect("slow_falling", 20, 6, true),
            new StatusEffect("regeneration", 20, 100, true)
        ));
        StartingGear = new ArrayList<>(List.of(
            new StartingItem("minecraft:stone_pickaxe", 1, true, false),
            new StartingItem("minecraft:stone_shovel", 1, true, false),
            new StartingItem("minecraft:stone_axe", 1, true, false),
            new StartingItem("minecraft:stone_sword", 1, true, false),
            new StartingItem("minecraft:bread", 64, true, false)
        ));
        ItemPools = new ArrayList<>(List.of(
                new OverworldEasyItemPool().Name
        ));
    }
}
