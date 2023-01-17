package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.Enchantment;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.OverworldNormalItemPool;

import java.util.ArrayList;
import java.util.List;

public class CursedPreset extends GamePreset {

    public CursedPreset() {
        Name = "Overworld - Cursed";
        Effects = new ArrayList<>(List.of(
            new StatusEffect("slow_falling", 20, 6, true),
            new StatusEffect("regeneration", 20, 100, true),
            new StatusEffect("speed", 99999, 255, false, false, false, true),
            new StatusEffect("blindness", 99999, 100, false, false, false, true)
        ));
        StartingGear = new ArrayList<>(List.of(
            new StartingItem("minecraft:iron_pickaxe", 1, true, false),
            new StartingItem("minecraft:iron_shovel", 1, true, false),
            new StartingItem("minecraft:iron_axe", 1, true, false),
            new StartingItem("minecraft:iron_sword", 1, true, false),
            new StartingItem("minecraft:bread", 64, true, false),
            new StartingItem("minecraft:chainmail_boots", 1, true, true, new ArrayList<>(List.of(
                    new Enchantment("minecraft:depth_strider", 10),
                    new Enchantment("minecraft:unbreaking", 10),
                    new Enchantment("minecraft:binding_curse", 1),
                    new Enchantment("minecraft:vanishing_curse", 1)
            )))
        ));
        ItemPools = new ArrayList<>(List.of(
                new OverworldNormalItemPool().Name
        ));
    }
}
