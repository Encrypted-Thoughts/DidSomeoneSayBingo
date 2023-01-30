package encrypted.dssb.config.itempools.defaultpools;

import encrypted.dssb.config.itempools.ItemGroup;
import encrypted.dssb.config.itempools.ItemPool;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

public class AllItemPool extends ItemPool {

    public ArrayList<String> CreativeOnly = new ArrayList<>(List.of(
            "minecraft:air",
            "minecraft:warden_spawn_egg",
            "minecraft:petrified_oak_slab",
            "minecraft:zombie_horse_spawn_egg",
            "minecraft:command_block_minecart",
            "minecraft:slime_spawn_egg",
            "minecraft:ghast_spawn_egg",
            "minecraft:repeating_command_block",
            "minecraft:panda_spawn_egg",
            "minecraft:wither_skeleton_spawn_egg",
            "minecraft:creeper_spawn_egg",
            "minecraft:crimson_hyphae",
            "minecraft:skeleton_spawn_egg",
            "minecraft:ocelot_spawn_egg",
            "minecraft:villager_spawn_egg",
            "minecraft:infested_cracked_stone_bricks",
            "minecraft:hoglin_spawn_egg",
            "minecraft:chain_command_block",
            "minecraft:piglin_spawn_egg",
            "minecraft:ender_dragon_spawn_egg",
            "minecraft:allay_spawn_egg",
            "minecraft:strider_spawn_egg",
            "minecraft:zombie_villager_spawn_egg",
            "minecraft:chicken_spawn_egg",
            "minecraft:phantom_spawn_egg",
            "minecraft:drowned_spawn_egg",
            "minecraft:squid_spawn_egg",
            "minecraft:silverfish_spawn_egg",
            "minecraft:horse_spawn_egg",
            "minecraft:witch_spawn_egg",
            "minecraft:wither_spawn_egg",
            "minecraft:infested_chiseled_stone_bricks",
            "minecraft:mule_spawn_egg",
            "minecraft:iron_golem_spawn_egg",
            "minecraft:pufferfish_spawn_egg",
            "minecraft:zombified_piglin_spawn_egg",
            "minecraft:magma_cube_spawn_egg",
            "minecraft:mooshroom_spawn_egg",
            "minecraft:turtle_spawn_egg",
            "minecraft:piglin_brute_spawn_egg",
            "minecraft:structure_void",
            "minecraft:stray_spawn_egg",
            "minecraft:axolotl_spawn_egg",
            "minecraft:wolf_spawn_egg",
            "minecraft:parrot_spawn_egg",
            "minecraft:polar_bear_spawn_egg",
            "minecraft:camel_spawn_egg",
            "minecraft:bat_spawn_egg",
            "minecraft:fox_spawn_egg",
            "minecraft:reinforced_deepslate",
            "minecraft:infested_deepslate",
            "minecraft:rabbit_spawn_egg",
            "minecraft:dolphin_spawn_egg",
            "minecraft:vex_spawn_egg",
            "minecraft:tadpole_spawn_egg",
            "minecraft:cow_spawn_egg",
            "minecraft:enderman_spawn_egg",
            "minecraft:skeleton_horse_spawn_egg",
            "minecraft:elder_guardian_spawn_egg",
            "minecraft:command_block",
            "minecraft:goat_spawn_egg",
            "minecraft:debug_stick",
            "minecraft:llama_spawn_egg",
            "minecraft:zoglin_spawn_egg",
            "minecraft:cat_spawn_egg",
            "minecraft:donkey_spawn_egg",
            "minecraft:snow_golem_spawn_egg",
            "minecraft:spider_spawn_egg",
            "minecraft:husk_spawn_egg",
            "minecraft:trader_llama_spawn_egg",
            "minecraft:player_head",
            "minecraft:endermite_spawn_egg",
            "minecraft:infested_stone",
            "minecraft:tropical_fish_spawn_egg",
            "minecraft:infested_mossy_stone_bricks",
            "minecraft:shulker_spawn_egg",
            "minecraft:bee_spawn_egg",
            "minecraft:infested_stone_bricks",
            "minecraft:evoker_spawn_egg",
            "minecraft:frog_spawn_egg",
            "minecraft:infested_cobblestone",
            "minecraft:cave_spider_spawn_egg",
            "minecraft:frogspawn",
            "minecraft:glow_squid_spawn_egg",
            "minecraft:cod_spawn_egg",
            "minecraft:guardian_spawn_egg",
            "minecraft:zombie_spawn_egg",
            "minecraft:ravager_spawn_egg",
            "minecraft:vindicator_spawn_egg",
            "minecraft:pig_spawn_egg",
            "minecraft:blaze_spawn_egg",
            "minecraft:sheep_spawn_egg",
            "minecraft:pillager_spawn_egg",
            "minecraft:wandering_trader_spawn_egg",
            "minecraft:salmon_spawn_egg",
            "minecraft:spawner",
            "minecraft:jigsaw",
            "minecraft:knowledge_book",
            "minecraft:light",
            "minecraft:budding_amethyst"
    ));

    public AllItemPool() {
        Name = "All";
        Items = new ArrayList<>();
        for (var item : Registries.ITEM.getKeys()) {
            var itemId = item.getValue().toString();
            if (!CreativeOnly.contains(itemId))
                Items.add(new ItemGroup(new String[]{itemId}));
        }
        Items = getItemsWithAvailableAsset(Items);
    }
}
