package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.Enchantment;
import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.OverworldNormalItemPool;

import java.util.ArrayList;
import java.util.List;

public class SuperBingoProfileConfig extends GameProfileConfig {

    public SuperBingoProfileConfig() {
        Name = "Super Bingo";
        Effects = new ArrayList<>(List.of(
            new StatusEffect("slow_falling", 20, 6, true),
                new StatusEffect("regeneration", 99999, 100, false, false, false, true),
                new StatusEffect("fire_resistance", 99999, 100, false, false, false, true),
                new StatusEffect("conduit_power", 99999, 20, false, false, false, true),
                new StatusEffect("night_vision", 99999, 100, false, false, false, true),
                new StatusEffect("speed", 99999, 6, false, false, false, true),
                new StatusEffect("jump_boost", 99999, 2, false, false, false, true),
                new StatusEffect("strength", 99999, 3, false, false, false, true)
        ));
        StartingGear = new ArrayList<>(List.of(
            new StartingItem("minecraft:netherite_pickaxe", 1, true, false),
            new StartingItem("minecraft:netherite_shovel", 1, true, false),
            new StartingItem("minecraft:netherite_axe", 1, true, false),
            new StartingItem("minecraft:netherite_sword", 1, true, false),
            new StartingItem("minecraft:golden_carrot", 64, true, false),
            new StartingItem("minecraft:netherite_helmet", 1, true, true),
            new StartingItem("minecraft:netherite_chestplate", 1, true, true),
            new StartingItem("minecraft:netherite_leggings", 1, true, true),
            new StartingItem("minecraft:netherite_boots", 1, true, true, new ArrayList<>(List.of(
                    new Enchantment("minecraft:depth_strider", 6)
            )))
        ));
        ItemPools = new ArrayList<>(List.of(
                new OverworldNormalItemPool().Name
        ));
    }
}
