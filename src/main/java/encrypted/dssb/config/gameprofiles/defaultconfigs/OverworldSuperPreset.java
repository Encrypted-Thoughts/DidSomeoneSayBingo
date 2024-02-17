package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.Enchantment;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.config.itempools.defaultpools.OverworldNormalItemPool;

import java.util.ArrayList;
import java.util.List;

public class OverworldSuperPreset extends GamePreset {

    public OverworldSuperPreset() {
        Name = "Overworld - Super";
        GameMode = "blackout";
        Effects = new ArrayList<>(List.of(
            new StatusEffect("minecraft:slow_falling", 20, 6, true),
                new StatusEffect("minecraft:regeneration", -1, 100, false, false, false, true),
                new StatusEffect("minecraft:fire_resistance", -1, 100, false, false, false, true),
                new StatusEffect("minecraft:conduit_power", -1, 20, false, false, false, true),
                new StatusEffect("minecraft:night_vision", -1, 100, false, false, false, true),
                new StatusEffect("minecraft:speed", -1, 6, false, false, false, true),
                new StatusEffect("minecraft:jump_boost", -1, 2, false, false, false, true),
                new StatusEffect("minecraft:strength", -1, 3, false, false, false, true)
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
