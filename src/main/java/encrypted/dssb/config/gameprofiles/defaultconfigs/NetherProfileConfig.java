package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.PossibleItemGroup;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;

import java.util.ArrayList;
import java.util.Arrays;

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
        Items = new ArrayList<>(Arrays.asList(
                new PossibleItemGroup(new String[]{
                        "soul_sand",
                        "soul_soil"
                }),
                new PossibleItemGroup(new String[]{"ender_pearl"}),
                new PossibleItemGroup(new String[]{"spectral_arrow"}),
                new PossibleItemGroup(new String[]{"soul_lantern"}),
                new PossibleItemGroup(new String[]{
                        "crimson_roots",
                        "warped_roots"
                }),
                new PossibleItemGroup(new String[]{
                        "iron_boots",
                        "iron_chestplate",
                        "iron_helmet",
                        "iron_leggings",
                        "iron_ingot",
                        "iron_block",
                        "iron_nugget",
                        "iron_hoe"
                }),
                new PossibleItemGroup(new String[]{
                        "bone",
                        "bone_meal",
                        "bone_block"
                }),
                new PossibleItemGroup(new String[]{"soul_campfire"}),
                new PossibleItemGroup(new String[]{"lava_bucket"}),
                new PossibleItemGroup(new String[]{"rotten_flesh"}),
                new PossibleItemGroup(new String[]{"nether_sprouts"}),
                new PossibleItemGroup(new String[]{"blaze_powder"}),
                new PossibleItemGroup(new String[]{"arrow"}),
                new PossibleItemGroup(new String[]{
                        "diamond_boots",
                        "diamond_chestplate",
                        "diamond_leggings",
                        "diamond_helmet"
                }),
                new PossibleItemGroup(new String[]{
                        "crimson_stem",
                        "crimson_planks",
                        "crimson_door",
                        "crimson_sign",
                        "crimson_trapdoor",
                        "stripped_crimson_stem"
                }),
                new PossibleItemGroup(new String[]{"wither_skeleton_skull"}),
                new PossibleItemGroup(new String[]{"leather"}),
                new PossibleItemGroup(new String[]{"magma_cream"}),
                new PossibleItemGroup(new String[]{
                        "porkchop",
                        "cooked_porkchop"
                }),
                new PossibleItemGroup(new String[]{
                        "golden_chestplate",
                        "golden_helmet",
                        "golden_leggings",
                        "golden_boots"
                }),
                new PossibleItemGroup(new String[]{
                        "diamond",
                        "diamond_block"
                }),
                new PossibleItemGroup(new String[]{
                        "crimson_hanging_sign",
                        "warped_hanging_sign"
                }),
                new PossibleItemGroup(new String[]{
                        "bow",
                        "crossbow"
                }),
                new PossibleItemGroup(new String[]{
                        "fishing_rod",
                        "warped_fungus_on_a_stick"
                }),
                new PossibleItemGroup(new String[]{
                        "blackstone",
                        "polished_blackstone",
                        "polished_blackstone_bricks",
                        "cracked_polished_blackstone_bricks",
                        "chiseled_polished_blackstone"
                }),
                new PossibleItemGroup(new String[]{
                        "basalt",
                        "polished_basalt"
                }),
                new PossibleItemGroup(new String[]{"string"}),
                new PossibleItemGroup(new String[]{"glowstone"}),
                new PossibleItemGroup(new String[]{"ghast_tear"}),
                new PossibleItemGroup(new String[]{
                        "quartz",
                        "quartz_block",
                        "quartz_bricks",
                        "quartz_pillar",
                        "chiseled_quartz_block"
                }),
                new PossibleItemGroup(new String[]{
                        "warped_stem",
                        "warped_planks",
                        "warped_door",
                        "warped_sign",
                        "warped_trapdoor",
                        "stripped_warped_stem"
                }),
                new PossibleItemGroup(new String[]{
                        "gold_block",
                        "gold_ingot",
                        "gold_nugget"
                }),
                new PossibleItemGroup(new String[]{
                        "obsidian",
                        "crying_obsidian"
                }),
                new PossibleItemGroup(new String[]{
                        "golden_axe",
                        "golden_hoe",
                        "golden_pickaxe",
                        "golden_shovel",
                        "golden_sword"
                }),
                new PossibleItemGroup(new String[]{
                        "warped_fungus",
                        "crimson_fungus"
                }),
                new PossibleItemGroup(new String[]{"shroomlight"}),
                new PossibleItemGroup(new String[]{
                        "hopper",
                        "iron_door",
                        "iron_bars",
                        "iron_trapdoor",
                        "smithing_table",
                        "chain",
                        "tripwire_hook"
                }),
                new PossibleItemGroup(new String[]{"soul_torch"}),
                new PossibleItemGroup(new String[]{
                        "nether_brick",
                        "red_nether_bricks",
                        "nether_bricks",
                        "chiseled_nether_bricks",
                        "cracked_nether_bricks"
                }),
                new PossibleItemGroup(new String[]{"name_tag"}),
                new PossibleItemGroup(new String[]{"blaze_rod"}),
                new PossibleItemGroup(new String[]{"diamond_hoe"}),
                new PossibleItemGroup(new String[]{
                        "weeping_vines",
                        "twisting_vines"
                }),
                new PossibleItemGroup(new String[]{
                        "gilded_blackstone"
                }),
                new PossibleItemGroup(new String[]{
                        "nether_wart_block",
                        "nether_wart",
                        "warped_wart_block"
                })
        ));

        Items = getItemsWithAvailableAsset(Items);
    }
}
