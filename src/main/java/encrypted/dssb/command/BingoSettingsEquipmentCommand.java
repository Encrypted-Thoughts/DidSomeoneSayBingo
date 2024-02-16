package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.BingoManager;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsEquipmentCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsEquipmentCommand = "equipment";
        var settingsEquipmentNoneCommand = "none";
        var settingsEquipmentStoneCommand = "stone";
        var settingsEquipmentIronCommand = "iron";
        var settingsEquipmentDiamondCommand = "diamond";
        var settingsEquipmentFoodCommand = "food";
        var settingsEquipmentAddCommand = "add";
        var settingsEquipmentRemoveCommand = "remove";
        var settingsEquipmentItemArgument = "item";
        var settingsEquipmentAmountArgument = "amount";
        var settingsEquipmentRespawnArgument = "respawn";
        var settingsEquipmentEquipArgument = "equip";
        var settingsEquipmentEnabledArgument = "enabled";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsEquipmentCommand)
                                        .then(literal(settingsEquipmentNoneCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.equipment.set_to").append(TranslationHelper.getAsText("dssb.commands.settings.equipment.none")));
                                                    BingoManager.GameSettings.StartingGear = new ArrayList<>();
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsEquipmentStoneCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.equipment.set_to").append(TranslationHelper.getAsText("dssb.commands.settings.equipment.stone")));

                                                    BingoManager.GameSettings.StartingGear.removeIf(gear ->
                                                            gear.Name.contains("_pickaxe") ||
                                                                    gear.Name.contains("_shovel") ||
                                                                    gear.Name.contains("_axe") ||
                                                                    gear.Name.contains("_sword"));

                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:stone_pickaxe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:stone_shovel", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:stone_axe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:stone_sword", 1, true, false));
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsEquipmentIronCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.equipment.set_to").append(TranslationHelper.getAsText("dssb.commands.settings.equipment.iron")));
                                                    BingoManager.GameSettings.StartingGear.removeIf(gear ->
                                                            gear.Name.contains("_pickaxe") ||
                                                                    gear.Name.contains("_shovel") ||
                                                                    gear.Name.contains("_axe") ||
                                                                    gear.Name.contains("_sword"));

                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:iron_pickaxe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:iron_shovel", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:iron_axe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:iron_sword", 1, true, false));
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsEquipmentDiamondCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.equipment.set_to").append(TranslationHelper.getAsText("dssb.commands.settings.equipment.diamond")));
                                                    BingoManager.GameSettings.StartingGear.removeIf(gear ->
                                                            gear.Name.contains("_pickaxe") ||
                                                                    gear.Name.contains("_shovel") ||
                                                                    gear.Name.contains("_axe") ||
                                                                    gear.Name.contains("_sword"));

                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:diamond_pickaxe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:diamond_shovel", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:diamond_axe", 1, true, false));
                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:diamond_sword", 1, true, false));
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsEquipmentFoodCommand)
                                                .then(argument(settingsEquipmentEnabledArgument, BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            var bool = BoolArgumentType.getBool(ctx, settingsEquipmentEnabledArgument);
                                                            var text = TranslationHelper.getAsText(bool ? "dssb.commands.settings.equipment.food.given" : "dssb.commands.settings.equipment.food.not_given");
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            BingoManager.GameSettings.StartingGear.removeIf(gear -> gear.Name.equals("minecraft:bread"));
                                                            if (bool)
                                                                BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:bread", 64, true, false));

                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(literal(settingsEquipmentAddCommand)
                                                .then(argument(settingsEquipmentItemArgument, ItemStackArgumentType.itemStack(registryAccess))
                                                        .then(argument(settingsEquipmentAmountArgument, IntegerArgumentType.integer(1))
                                                                .then(argument(settingsEquipmentRespawnArgument, BoolArgumentType.bool())
                                                                        .then(argument(settingsEquipmentEquipArgument, BoolArgumentType.bool())
                                                                                .executes(ctx -> {
                                                                                    var item = ItemStackArgumentType.getItemStackArgument(ctx, settingsEquipmentItemArgument);
                                                                                    var itemName = item.asString();

                                                                                    var count = IntegerArgumentType.getInteger(ctx, settingsEquipmentAmountArgument);
                                                                                    var onRespawn = BoolArgumentType.getBool(ctx, settingsEquipmentRespawnArgument);
                                                                                    var equip = BoolArgumentType.getBool(ctx, settingsEquipmentEquipArgument);

                                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                                            TranslationHelper.getAsText("dssb.commands.settings.equipment.add.item_added", itemName, count, onRespawn, equip));
                                                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem(itemName, count, onRespawn, equip));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                }))))))
                                        .then(literal(settingsEquipmentRemoveCommand)
                                                .then(argument(settingsEquipmentItemArgument, ItemStackArgumentType.itemStack(registryAccess))
                                                        .executes(ctx -> {
                                                            var item = ItemStackArgumentType.getItemStackArgument(ctx, settingsEquipmentItemArgument);
                                                            var itemName = item.asString();
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                    TranslationHelper.getAsText("dssb.commands.settings.equipment.remove.item_removed", itemName));
                                                            BingoManager.GameSettings.StartingGear.removeIf(gear -> gear.Name.equals(itemName));

                                                            return Command.SINGLE_SUCCESS;
                                                        }))))));
    }
}
