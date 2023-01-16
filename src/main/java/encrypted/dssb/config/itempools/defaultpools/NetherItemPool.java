package encrypted.dssb.config.itempools.defaultpools;

import encrypted.dssb.config.itempools.ItemGroup;
import encrypted.dssb.config.itempools.ItemPool;

import java.util.ArrayList;
import java.util.List;

public class NetherItemPool extends ItemPool {
    public NetherItemPool() {
        Name = "Nether";
        Items = new ArrayList<>(List.of(
                new ItemGroup(new String[]{
                        "soul_sand",
                        "soul_soil"
                }),
                new ItemGroup(new String[]{"ender_pearl"}),
                new ItemGroup(new String[]{"spectral_arrow"}),
                new ItemGroup(new String[]{"soul_lantern"}),
                new ItemGroup(new String[]{
                        "crimson_roots",
                        "warped_roots"
                }),
                new ItemGroup(new String[]{
                        "iron_boots",
                        "iron_chestplate",
                        "iron_helmet",
                        "iron_leggings",
                        "iron_ingot",
                        "iron_block",
                        "iron_nugget",
                        "iron_hoe"
                }),
                new ItemGroup(new String[]{
                        "bone",
                        "bone_meal",
                        "bone_block"
                }),
                new ItemGroup(new String[]{"soul_campfire"}),
                new ItemGroup(new String[]{"lava_bucket"}),
                new ItemGroup(new String[]{"rotten_flesh"}),
                new ItemGroup(new String[]{"nether_sprouts"}),
                new ItemGroup(new String[]{"blaze_powder"}),
                new ItemGroup(new String[]{"arrow"}),
                new ItemGroup(new String[]{
                        "diamond_boots",
                        "diamond_chestplate",
                        "diamond_leggings",
                        "diamond_helmet"
                }),
                new ItemGroup(new String[]{
                        "crimson_stem",
                        "crimson_planks",
                        "crimson_door",
                        "crimson_sign",
                        "crimson_trapdoor",
                        "stripped_crimson_stem"
                }),
                new ItemGroup(new String[]{"wither_skeleton_skull"}),
                new ItemGroup(new String[]{"leather"}),
                new ItemGroup(new String[]{"magma_cream"}),
                new ItemGroup(new String[]{
                        "porkchop",
                        "cooked_porkchop"
                }),
                new ItemGroup(new String[]{
                        "golden_chestplate",
                        "golden_helmet",
                        "golden_leggings",
                        "golden_boots"
                }),
                new ItemGroup(new String[]{
                        "diamond",
                        "diamond_block"
                }),
                new ItemGroup(new String[]{
                        "crimson_hanging_sign",
                        "warped_hanging_sign"
                }),
                new ItemGroup(new String[]{
                        "bow",
                        "crossbow"
                }),
                new ItemGroup(new String[]{
                        "fishing_rod",
                        "warped_fungus_on_a_stick"
                }),
                new ItemGroup(new String[]{
                        "blackstone",
                        "polished_blackstone",
                        "polished_blackstone_bricks",
                        "cracked_polished_blackstone_bricks",
                        "chiseled_polished_blackstone"
                }),
                new ItemGroup(new String[]{
                        "basalt",
                        "polished_basalt"
                }),
                new ItemGroup(new String[]{"string"}),
                new ItemGroup(new String[]{"glowstone"}),
                new ItemGroup(new String[]{"ghast_tear"}),
                new ItemGroup(new String[]{
                        "quartz",
                        "quartz_block",
                        "quartz_bricks",
                        "quartz_pillar",
                        "chiseled_quartz_block"
                }),
                new ItemGroup(new String[]{
                        "warped_stem",
                        "warped_planks",
                        "warped_door",
                        "warped_sign",
                        "warped_trapdoor",
                        "stripped_warped_stem"
                }),
                new ItemGroup(new String[]{
                        "gold_block",
                        "gold_ingot",
                        "gold_nugget"
                }),
                new ItemGroup(new String[]{
                        "obsidian",
                        "crying_obsidian"
                }),
                new ItemGroup(new String[]{
                        "golden_axe",
                        "golden_hoe",
                        "golden_pickaxe",
                        "golden_shovel",
                        "golden_sword"
                }),
                new ItemGroup(new String[]{
                        "warped_fungus",
                        "crimson_fungus"
                }),
                new ItemGroup(new String[]{"shroomlight"}),
                new ItemGroup(new String[]{
                        "hopper",
                        "iron_door",
                        "iron_bars",
                        "iron_trapdoor",
                        "smithing_table",
                        "chain",
                        "tripwire_hook"
                }),
                new ItemGroup(new String[]{"soul_torch"}),
                new ItemGroup(new String[]{
                        "nether_brick",
                        "red_nether_bricks",
                        "nether_bricks",
                        "chiseled_nether_bricks",
                        "cracked_nether_bricks"
                }),
                new ItemGroup(new String[]{"name_tag"}),
                new ItemGroup(new String[]{"blaze_rod"}),
                new ItemGroup(new String[]{"diamond_hoe"}),
                new ItemGroup(new String[]{
                        "weeping_vines",
                        "twisting_vines"
                }),
                new ItemGroup(new String[]{
                        "gilded_blackstone"
                }),
                new ItemGroup(new String[]{
                        "nether_wart_block",
                        "nether_wart",
                        "warped_wart_block"
                })
        ));

        Items = getItemsWithAvailableAsset(Items);
    }
}
