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
                        "minecraft:soul_sand",
                        "minecraft:soul_soil"
                }),
                new ItemGroup(new String[]{"minecraft:ender_pearl"}),
                new ItemGroup(new String[]{"minecraft:spectral_arrow"}),
                new ItemGroup(new String[]{"minecraft:soul_lantern"}),
                new ItemGroup(new String[]{
                        "minecraft:crimson_roots",
                        "minecraft:warped_roots"
                }),
                new ItemGroup(new String[]{
                        "minecraft:iron_boots",
                        "minecraft:iron_chestplate",
                        "minecraft:iron_helmet",
                        "minecraft:iron_leggings",
                        "minecraft:iron_ingot",
                        "minecraft:iron_block",
                        "minecraft:iron_nugget",
                        "minecraft:iron_hoe"
                }),
                new ItemGroup(new String[]{
                        "minecraft:bone",
                        "minecraft:bone_meal",
                        "minecraft:bone_block"
                }),
                new ItemGroup(new String[]{"minecraft:soul_campfire"}),
                new ItemGroup(new String[]{"minecraft:lava_bucket"}),
                new ItemGroup(new String[]{"minecraft:rotten_flesh"}),
                new ItemGroup(new String[]{"minecraft:nether_sprouts"}),
                new ItemGroup(new String[]{"minecraft:blaze_powder"}),
                new ItemGroup(new String[]{"minecraft:arrow"}),
                new ItemGroup(new String[]{
                        "minecraft:diamond_boots",
                        "minecraft:diamond_chestplate",
                        "minecraft:diamond_leggings",
                        "minecraft:diamond_helmet"
                }),
                new ItemGroup(new String[]{
                        "minecraft:crimson_stem",
                        "minecraft:crimson_planks",
                        "minecraft:crimson_door",
                        "minecraft:crimson_sign",
                        "minecraft:crimson_trapdoor",
                        "minecraft:stripped_crimson_stem"
                }),
                new ItemGroup(new String[]{"minecraft:wither_skeleton_skull"}),
                new ItemGroup(new String[]{"minecraft:leather"}),
                new ItemGroup(new String[]{"minecraft:magma_cream"}),
                new ItemGroup(new String[]{
                        "minecraft:porkchop",
                        "minecraft:cooked_porkchop"
                }),
                new ItemGroup(new String[]{
                        "minecraft:golden_chestplate",
                        "minecraft:golden_helmet",
                        "minecraft:golden_leggings",
                        "minecraft:golden_boots"
                }),
                new ItemGroup(new String[]{
                        "minecraft:diamond",
                        "minecraft:diamond_block"
                }),
                new ItemGroup(new String[]{
                        "minecraft:crimson_hanging_sign",
                        "minecraft:warped_hanging_sign"
                }),
                new ItemGroup(new String[]{
                        "minecraft:bow",
                        "minecraft:crossbow"
                }),
                new ItemGroup(new String[]{
                        "minecraft:fishing_rod",
                        "minecraft:warped_fungus_on_a_stick"
                }),
                new ItemGroup(new String[]{
                        "minecraft:blackstone",
                        "minecraft:polished_blackstone",
                        "minecraft:polished_blackstone_bricks",
                        "minecraft:cracked_polished_blackstone_bricks",
                        "minecraft:chiseled_polished_blackstone"
                }),
                new ItemGroup(new String[]{
                        "minecraft:basalt",
                        "minecraft:polished_basalt"
                }),
                new ItemGroup(new String[]{"minecraft:string"}),
                new ItemGroup(new String[]{"minecraft:glowstone"}),
                new ItemGroup(new String[]{"minecraft:ghast_tear"}),
                new ItemGroup(new String[]{
                        "minecraft:quartz",
                        "minecraft:quartz_block",
                        "minecraft:quartz_bricks",
                        "minecraft:quartz_pillar",
                        "minecraft:chiseled_quartz_block"
                }),
                new ItemGroup(new String[]{
                        "minecraft:warped_stem",
                        "minecraft:warped_planks",
                        "minecraft:warped_door",
                        "minecraft:warped_sign",
                        "minecraft:warped_trapdoor",
                        "minecraft:stripped_warped_stem"
                }),
                new ItemGroup(new String[]{
                        "minecraft:gold_block",
                        "minecraft:gold_ingot",
                        "minecraft:gold_nugget"
                }),
                new ItemGroup(new String[]{
                        "minecraft:obsidian",
                        "minecraft:crying_obsidian"
                }),
                new ItemGroup(new String[]{
                        "minecraft:golden_axe",
                        "minecraft:golden_hoe",
                        "minecraft:golden_pickaxe",
                        "minecraft:golden_shovel",
                        "minecraft:golden_sword"
                }),
                new ItemGroup(new String[]{
                        "minecraft:warped_fungus",
                        "minecraft:crimson_fungus"
                }),
                new ItemGroup(new String[]{"shroomlight"}),
                new ItemGroup(new String[]{
                        "minecraft:hopper",
                        "minecraft:iron_door",
                        "minecraft:iron_bars",
                        "minecraft:iron_trapdoor",
                        "minecraft:smithing_table",
                        "minecraft:chain",
                        "minecraft:tripwire_hook"
                }),
                new ItemGroup(new String[]{"minecraft:soul_torch"}),
                new ItemGroup(new String[]{
                        "minecraft:nether_brick",
                        "minecraft:red_nether_bricks",
                        "minecraft:nether_bricks",
                        "minecraft:chiseled_nether_bricks",
                        "minecraft:cracked_nether_bricks"
                }),
                new ItemGroup(new String[]{"minecraft:name_tag"}),
                new ItemGroup(new String[]{"minecraft:blaze_rod"}),
                new ItemGroup(new String[]{"minecraft:diamond_hoe"}),
                new ItemGroup(new String[]{
                        "minecraft:weeping_vines",
                        "minecraft:twisting_vines"
                }),
                new ItemGroup(new String[]{
                        "minecraft:gilded_blackstone"
                }),
                new ItemGroup(new String[]{
                        "minecraft:nether_wart_block",
                        "minecraft:nether_wart",
                        "minecraft:warped_wart_block"
                })
        ));

        Items = getItemsWithAvailableAsset(Items);
    }
}
