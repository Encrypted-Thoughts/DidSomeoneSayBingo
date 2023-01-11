package encrypted.dssb.config.gameprofiles.defaultconfigs;

import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.PossibleItemGroup;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;

import java.util.ArrayList;
import java.util.Arrays;

public class EasyProfileConfig extends GameProfileConfig {
    public EasyProfileConfig() {
        Name = "Overworld - Easy";
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
        Items = new ArrayList<>(Arrays.asList(
                new PossibleItemGroup(new String[]{
                        "raw_copper_block",
                        "raw_copper"
                }),
                new PossibleItemGroup(new String[]{"compass"}),
                new PossibleItemGroup(new String[]{"crossbow"}),
                new PossibleItemGroup(new String[]{
                        "golden_chestplate",
                        "golden_helmet",
                        "golden_leggings",
                        "golden_boots"
                }),
                new PossibleItemGroup(new String[]{"barrel"}),
                new PossibleItemGroup(new String[]{"dispenser"}),
                new PossibleItemGroup(new String[]{"fletching_table"}),
                new PossibleItemGroup(new String[]{"ender_pearl"}),
                new PossibleItemGroup(new String[]{"firework_rocket"}),
                new PossibleItemGroup(new String[]{
                        "gold_block",
                        "gold_ingot",
                        "gold_nugget"
                }),
                new PossibleItemGroup(new String[]{
                        "bucket",
                        "lava_bucket",
                        "water_bucket",
                        "axolotl_bucket",
                        "tadpole_bucket",
                        "milk_bucket",
                        "powder_snow_bucket"
                }),
                new PossibleItemGroup(new String[]{
                        "redstone",
                        "redstone_block"
                }),
                new PossibleItemGroup(new String[]{
                        "mossy_cobblestone",
                        "mossy_stone_bricks"
                }),
                new PossibleItemGroup(new String[]{"egg"}),
                new PossibleItemGroup(new String[]{"iron_bars"}),
                new PossibleItemGroup(new String[]{"wooden_sword"}),
                new PossibleItemGroup(new String[]{"target"}),
                new PossibleItemGroup(new String[]{"writable_book"}),
                new PossibleItemGroup(new String[]{"lightning_rod"}),
                new PossibleItemGroup(new String[]{"cracked_deepslate_bricks"}),
                new PossibleItemGroup(new String[]{"smooth_stone"}),
                new PossibleItemGroup(new String[]{"lily_pad"}),
                new PossibleItemGroup(new String[]{"repeater"}),
                new PossibleItemGroup(new String[]{"lantern"}),
                new PossibleItemGroup(new String[]{
                        "bone",
                        "bone_meal",
                        "bone_block"
                }),
                new PossibleItemGroup(new String[]{
                        "coal",
                        "coal_block"
                }),
                new PossibleItemGroup(new String[]{"glass"}),
                new PossibleItemGroup(new String[]{
                        "rabbit",
                        "cooked_rabbit",
                        "rabbit_hide",
                        "rabbit_foot",
                        "rabbit_stew"
                }),
                new PossibleItemGroup(new String[]{"jukebox"}),
                new PossibleItemGroup(new String[]{"glass_bottle"}),
                new PossibleItemGroup(new String[]{"chain"}),
                new PossibleItemGroup(new String[]{
                        "mud_bricks",
                        "mud",
                        "packed_mud"
                }),
                new PossibleItemGroup(new String[]{
                        "sugar",
                        "sugar_cane"
                }),
                new PossibleItemGroup(new String[]{"leather"}),
                new PossibleItemGroup(new String[]{"red_mushroom"}),
                new PossibleItemGroup(new String[]{"sweet_berries"}),
                new PossibleItemGroup(new String[]{
                        "dark_oak_boat",
                        "dark_oak_chest_boat",
                        "dark_oak_door",
                        "dark_oak_log",
                        "dark_oak_sapling",
                        "dark_oak_sign",
                        "dark_oak_planks",
                        "dark_oak_trapdoor",
                        "stripped_dark_oak_log"
                }),
                new PossibleItemGroup(new String[]{
                        "glow_ink_sac",
                        "ink_sac"
                }),
                new PossibleItemGroup(new String[]{
                        "iron_boots",
                        "iron_chestplate",
                        "iron_helmet",
                        "iron_leggings"
                }),
                new PossibleItemGroup(new String[]{"wheat_seeds"}),
                new PossibleItemGroup(new String[]{"clay"}),
                new PossibleItemGroup(new String[]{
                        "andesite",
                        "polished_andesite"
                }),
                new PossibleItemGroup(new String[]{"stone_hoe"}),
                new PossibleItemGroup(new String[]{"campfire"}),
                new PossibleItemGroup(new String[]{"wooden_pickaxe"}),
                new PossibleItemGroup(new String[]{"lead"}),
                new PossibleItemGroup(new String[]{"grindstone"}),
                new PossibleItemGroup(new String[]{
                        "oak_boat",
                        "oak_chest_boat",
                        "oak_door",
                        "oak_log",
                        "oak_planks",
                        "oak_sapling",
                        "oak_sign",
                        "oak_trapdoor",
                        "stripped_oak_log"
                }),
                new PossibleItemGroup(new String[]{"suspicious_stew"}),
                new PossibleItemGroup(new String[]{"ladder"}),
                new PossibleItemGroup(new String[]{"spider_eye"}),
                new PossibleItemGroup(new String[]{
                        "cracked_stone_bricks",
                        "chiseled_stone_bricks"
                }),
                new PossibleItemGroup(new String[]{
                        "mangrove_boat",
                        "mangrove_chest_boat",
                        "mangrove_door",
                        "mangrove_log",
                        "mangrove_planks",
                        "mangrove_propagule",
                        "mangrove_sign",
                        "mangrove_trapdoor",
                        "stripped_mangrove_log"
                }),
                new PossibleItemGroup(new String[]{
                        "black_wool",
                        "blue_wool",
                        "brown_wool",
                        "cyan_wool",
                        "gray_wool",
                        "green_wool",
                        "lime_wool",
                        "light_blue_wool",
                        "light_gray_wool",
                        "magenta_wool",
                        "orange_wool",
                        "pink_wool",
                        "purple_wool",
                        "red_wool",
                        "white_wool",
                        "yellow_wool"
                }),
                new PossibleItemGroup(new String[]{
                        "golden_axe",
                        "golden_hoe",
                        "golden_pickaxe",
                        "golden_shovel",
                        "golden_sword"
                }),
                new PossibleItemGroup(new String[]{"flint_and_steel"}),
                new PossibleItemGroup(new String[]{
                        "granite",
                        "polished_granite"
                }),
                new PossibleItemGroup(new String[]{
                        "raw_iron_block",
                        "raw_iron"
                }),
                new PossibleItemGroup(new String[]{"cauldron"}),
                new PossibleItemGroup(new String[]{"rooted_dirt"}),
                new PossibleItemGroup(new String[]{"red_sand"}),
                new PossibleItemGroup(new String[]{
                        "chicken",
                        "cooked_chicken"
                }),
                new PossibleItemGroup(new String[]{"spyglass"}),
                new PossibleItemGroup(new String[]{
                        "copper_block",
                        "cut_copper",
                        "copper_ingot"
                }),
                new PossibleItemGroup(new String[]{"calcite"}),
                new PossibleItemGroup(new String[]{"composter"}),
                new PossibleItemGroup(new String[]{"minecart"}),
                new PossibleItemGroup(new String[]{"arrow"}),
                new PossibleItemGroup(new String[]{"brown_mushroom"}),
                new PossibleItemGroup(new String[]{"furnace"}),
                new PossibleItemGroup(new String[]{"stone_sword"}),
                new PossibleItemGroup(new String[]{"anvil"}),
                new PossibleItemGroup(new String[]{
                        "dried_kelp",
                        "dried_kelp_block",
                        "kelp"
                }),
                new PossibleItemGroup(new String[]{"tripwire_hook"}),
                new PossibleItemGroup(new String[]{"powered_rail"}),
                new PossibleItemGroup(new String[]{"iron_door"}),
                new PossibleItemGroup(new String[]{"blast_furnace"}),
                new PossibleItemGroup(new String[]{"coarse_dirt"}),
                new PossibleItemGroup(new String[]{
                        "dripstone_block",
                        "pointed_dripstone"
                }),
                new PossibleItemGroup(new String[]{
                        "piston",
                        "sticky_piston"
                }),
                new PossibleItemGroup(new String[]{
                        "beef",
                        "cooked_beef"
                }),
                new PossibleItemGroup(new String[]{
                        "slime_ball",
                        "slime_block"
                }),
                new PossibleItemGroup(new String[]{"moss_block"}),
                new PossibleItemGroup(new String[]{
                        "black_glazed_terracotta",
                        "black_terracotta",
                        "blue_glazed_terracotta",
                        "blue_terracotta",
                        "brown_glazed_terracotta",
                        "brown_terracotta",
                        "cyan_glazed_terracotta",
                        "cyan_terracotta",
                        "gray_glazed_terracotta",
                        "gray_terracotta",
                        "green_glazed_terracotta",
                        "green_terracotta",
                        "light_blue_glazed_terracotta",
                        "light_blue_terracotta",
                        "light_gray_glazed_terracotta",
                        "light_gray_terracotta",
                        "lime_glazed_terracotta",
                        "lime_terracotta",
                        "magenta_glazed_terracotta",
                        "magenta_terracotta",
                        "orange_glazed_terracotta",
                        "orange_terracotta",
                        "pink_glazed_terracotta",
                        "pink_terracotta",
                        "purple_glazed_terracotta",
                        "purple_terracotta",
                        "red_glazed_terracotta",
                        "red_terracotta",
                        "white_glazed_terracotta",
                        "white_terracotta",
                        "yellow_glazed_terracotta",
                        "yellow_terracotta",
                        "terracotta"
                }),
                new PossibleItemGroup(new String[]{"hay_block"}),
                new PossibleItemGroup(new String[]{
                        "tropical_fish",
                        "tropical_fish_bucket"
                }),
                new PossibleItemGroup(new String[]{"polished_deepslate"}),
                new PossibleItemGroup(new String[]{
                        "salmon",
                        "cooked_salmon",
                        "salmon_bucket"
                }),
                new PossibleItemGroup(new String[]{"clay_ball"}),
                new PossibleItemGroup(new String[]{"note_block"}),
                new PossibleItemGroup(new String[]{
                        "carved_pumpkin",
                        "jack_o_lantern",
                        "pumpkin",
                        "pumpkin_seeds"
                }),
                new PossibleItemGroup(new String[]{
                        "acacia_boat",
                        "acacia_chest_boat",
                        "acacia_door",
                        "acacia_log",
                        "acacia_planks",
                        "stripped_acacia_log",
                        "acacia_sapling",
                        "acacia_sign",
                        "acacia_trapdoor"
                }),
                new PossibleItemGroup(new String[]{
                        "carrot",
                        "golden_carrot"
                }),
                new PossibleItemGroup(new String[]{
                        "amethyst_block",
                        "amethyst_shard"
                }),
                new PossibleItemGroup(new String[]{
                        "book",
                        "bookshelf",
                        "chiseled_bookshelf"
                }),
                new PossibleItemGroup(new String[]{"stonecutter"}),
                new PossibleItemGroup(new String[]{
                        "black_stained_glass",
                        "blue_stained_glass",
                        "brown_stained_glass",
                        "cyan_stained_glass",
                        "gray_stained_glass",
                        "green_stained_glass",
                        "light_blue_stained_glass",
                        "light_gray_stained_glass",
                        "lime_stained_glass",
                        "magenta_stained_glass",
                        "orange_stained_glass",
                        "pink_stained_glass",
                        "purple_stained_glass",
                        "red_stained_glass",
                        "white_stained_glass",
                        "yellow_stained_glass"
                }),
                new PossibleItemGroup(new String[]{"redstone_torch"}),
                new PossibleItemGroup(new String[]{"seagrass"}),
                new PossibleItemGroup(new String[]{
                        "acacia_hanging_sign",
                        "oak_hanging_sign",
                        "spruce_hanging_sign",
                        "birch_hanging_sign",
                        "jungle_hanging_sign",
                        "dark_oak_hanging_sign",
                        "mangrove_hanging_sign",
                        "bamboo_hanging_sign"
                }),
                new PossibleItemGroup(new String[]{"leather_horse_armor"}),
                new PossibleItemGroup(new String[]{"armor_stand"}),
                new PossibleItemGroup(new String[]{"redstone_lamp"}),
                new PossibleItemGroup(new String[]{"deepslate_bricks"}),
                new PossibleItemGroup(new String[]{"magma_block"}),
                new PossibleItemGroup(new String[]{"dandelion"}),
                new PossibleItemGroup(new String[]{
                        "red_sandstone",
                        "cut_red_sandstone",
                        "chiseled_red_sandstone"
                }),
                new PossibleItemGroup(new String[]{"sand"}),
                new PossibleItemGroup(new String[]{"wooden_axe"}),
                new PossibleItemGroup(new String[]{"hopper"}),
                new PossibleItemGroup(new String[]{"stick"}),
                new PossibleItemGroup(new String[]{"rotten_flesh"}),
                new PossibleItemGroup(new String[]{"cartography_table"}),
                new PossibleItemGroup(new String[]{"hanging_roots"}),
                new PossibleItemGroup(new String[]{"dirt"}),
                new PossibleItemGroup(new String[]{"fishing_rod"}),
                new PossibleItemGroup(new String[]{"flint"}),
                new PossibleItemGroup(new String[]{
                        "peony",
                        "pink_tulip",
                        "orange_tulip",
                        "oxeye_daisy",
                        "lilac",
                        "lily_of_the_valley",
                        "poppy",
                        "rose_bush",
                        "red_tulip",
                        "sunflower",
                        "white_tulip",
                        "blue_orchid",
                        "cornflower",
                        "azure_bluet",
                        "allium"
                }),
                new PossibleItemGroup(new String[]{"bell"}),
                new PossibleItemGroup(new String[]{"paper"}),
                new PossibleItemGroup(new String[]{
                        "chiseled_sandstone",
                        "cut_sandstone"
                }),
                new PossibleItemGroup(new String[]{"activator_rail"}),
                new PossibleItemGroup(new String[]{"golden_apple"}),
                new PossibleItemGroup(new String[]{"sandstone"}),
                new PossibleItemGroup(new String[]{"crafting_table"}),
                new PossibleItemGroup(new String[]{
                        "diorite",
                        "polished_diorite"
                }),
                new PossibleItemGroup(new String[]{"cake"}),
                new PossibleItemGroup(new String[]{"beetroot_soup"}),
                new PossibleItemGroup(new String[]{
                        "brick",
                        "bricks"
                }),
                new PossibleItemGroup(new String[]{"sea_pickle"}),
                new PossibleItemGroup(new String[]{
                        "jungle_boat",
                        "jungle_chest_boat",
                        "jungle_door",
                        "jungle_log",
                        "jungle_planks",
                        "jungle_sapling",
                        "jungle_sign",
                        "jungle_trapdoor",
                        "stripped_jungle_log"
                }),
                new PossibleItemGroup(new String[]{"wooden_shovel"}),
                new PossibleItemGroup(new String[]{
                        "pufferfish",
                        "pufferfish_bucket"
                }),
                new PossibleItemGroup(new String[]{"fermented_spider_eye"}),
                new PossibleItemGroup(new String[]{
                        "raw_gold_block",
                        "raw_gold"
                }),
                new PossibleItemGroup(new String[]{"goat_horn"}),
                new PossibleItemGroup(new String[]{"string"}),
                new PossibleItemGroup(new String[]{
                        "black_concrete",
                        "black_concrete_powder",
                        "blue_concrete",
                        "blue_concrete_powder",
                        "brown_concrete",
                        "brown_concrete_powder",
                        "cyan_concrete",
                        "cyan_concrete_powder",
                        "gray_concrete",
                        "gray_concrete_powder",
                        "green_concrete",
                        "green_concrete_powder",
                        "light_blue_concrete",
                        "light_blue_concrete_powder",
                        "light_gray_concrete",
                        "light_gray_concrete_powder",
                        "lime_concrete",
                        "lime_concrete_powder",
                        "magenta_concrete",
                        "magenta_concrete_powder",
                        "orange_concrete",
                        "orange_concrete_powder",
                        "pink_concrete",
                        "pink_concrete_powder",
                        "purple_concrete",
                        "purple_concrete_powder",
                        "red_concrete",
                        "red_concrete_powder",
                        "white_concrete",
                        "white_concrete_powder",
                        "yellow_concrete",
                        "yellow_concrete_powder"
                }),
                new PossibleItemGroup(new String[]{"tuff"}),
                new PossibleItemGroup(new String[]{
                        "white_dye",
                        "yellow_dye",
                        "red_dye",
                        "purple_dye",
                        "pink_dye",
                        "orange_dye",
                        "magenta_dye",
                        "lime_dye",
                        "light_blue_dye",
                        "light_gray_dye",
                        "gray_dye",
                        "green_dye",
                        "cyan_dye",
                        "blue_dye",
                        "brown_dye",
                        "black_dye"
                }),
                new PossibleItemGroup(new String[]{"feather"}),
                new PossibleItemGroup(new String[]{"smoker"}),
                new PossibleItemGroup(new String[]{"rail"}),
                new PossibleItemGroup(new String[]{"gravel"}),
                new PossibleItemGroup(new String[]{"tinted_glass"}),
                new PossibleItemGroup(new String[]{"bow"}),
                new PossibleItemGroup(new String[]{"bowl"}),
                new PossibleItemGroup(new String[]{"carrot_on_a_stick"}),
                new PossibleItemGroup(new String[]{"tnt"}),
                new PossibleItemGroup(new String[]{
                        "cod",
                        "cooked_cod",
                        "cod_bucket"
                }),
                new PossibleItemGroup(new String[]{"detector_rail"}),
                new PossibleItemGroup(new String[]{
                        "deepslate",
                        "cobbled_deepslate"
                }),
                new PossibleItemGroup(new String[]{"dropper"}),
                new PossibleItemGroup(new String[]{"charcoal"}),
                new PossibleItemGroup(new String[]{
                        "birch_boat",
                        "birch_chest_boat",
                        "birch_door",
                        "birch_log",
                        "birch_planks",
                        "birch_sapling",
                        "birch_sign",
                        "birch_trapdoor",
                        "stripped_birch_log"
                }),
                new PossibleItemGroup(new String[]{
                        "iron_ingot",
                        "iron_block",
                        "iron_nugget"
                }),
                new PossibleItemGroup(new String[]{
                        "mutton",
                        "cooked_mutton"
                }),
                new PossibleItemGroup(new String[]{
                        "azalea",
                        "flowering_azalea",
                        "azalea_leaves",
                        "flowering_azalea_leaves"
                }),
                new PossibleItemGroup(new String[]{
                        "porkchop",
                        "cooked_porkchop"
                }),
                new PossibleItemGroup(new String[]{"wheat"}),
                new PossibleItemGroup(new String[]{"shears"}),
                new PossibleItemGroup(new String[]{"wooden_hoe"}),
                new PossibleItemGroup(new String[]{"smithing_table"}),
                new PossibleItemGroup(new String[]{
                        "cobblestone",
                        "stone"
                }),
                new PossibleItemGroup(new String[]{"iron_trapdoor"}),
                new PossibleItemGroup(new String[]{
                        "potato",
                        "baked_potato"
                }),
                new PossibleItemGroup(new String[]{"gunpowder"}),
                new PossibleItemGroup(new String[]{
                        "beetroot",
                        "beetroot_seeds"
                }),
                new PossibleItemGroup(new String[]{
                        "spruce_boat",
                        "spruce_chest_boat",
                        "spruce_door",
                        "spruce_log",
                        "spruce_planks",
                        "spruce_sapling",
                        "spruce_sign",
                        "spruce_trapdoor",
                        "stripped_spruce_log"
                }),
                new PossibleItemGroup(new String[]{
                        "lapis_block",
                        "lapis_lazuli"
                }),
                new PossibleItemGroup(new String[]{
                        "snow",
                        "snowball"
                }),
                new PossibleItemGroup(new String[]{"stone_bricks"}),
                new PossibleItemGroup(new String[]{
                        "glow_item_frame",
                        "item_frame"
                }),
                new PossibleItemGroup(new String[]{"apple"}),
                new PossibleItemGroup(new String[]{"big_dripleaf"}),
                new PossibleItemGroup(new String[]{"glow_berries"}),
                new PossibleItemGroup(new String[]{"loom"}),
                new PossibleItemGroup(new String[]{"flower_pot"}),
                new PossibleItemGroup(new String[]{
                        "melon",
                        "melon_seeds",
                        "melon_slice"
                }),
                new PossibleItemGroup(new String[]{"fire_charge"}),
                new PossibleItemGroup(new String[]{"painting"}),
                new PossibleItemGroup(new String[]{"iron_hoe"}),
                new PossibleItemGroup(new String[]{"torch"}),
                new PossibleItemGroup(new String[]{"mushroom_stew"})
        ));

        Items = getItemsWithAvailableAsset(Items);
    }
}
