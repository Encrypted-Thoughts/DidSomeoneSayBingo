package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.StartingItem;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("settings")

                                // Display current settings
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayer();
                                    if (player != null)
                                        return tellSettings(player);
                                    return Command.SINGLE_SUCCESS;
                                })

                                // Chose a random profile
                                .then(literal("randomize")
                                        .executes(ctx -> randomizeSettings(ctx.getSource().getServer())))

                                // Set y offset on spawning during bingo game
                                .then(literal("ySpawnOffset")
                                        .then(argument("offset", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    GameSettings.YSpawnOffset = IntegerArgumentType.getInteger(ctx, "offset");
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set max y level for spawning in targeted bingo dimension
                                .then(literal("maxYLevel")
                                        .then(argument("maxy", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    GameSettings.MaxYLevel = IntegerArgumentType.getInteger(ctx, "maxy");
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set the game mode to play
                                .then(literal("gamemode")
                                        .then(literal("bingo")
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "bingo");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal("lockout")
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "lockout");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal("blackout")
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "blackout");
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set the equipment settings
                                .then(literal("equipment")
                                        .then(literal("none")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting equipment set to ").formatted(Formatting.WHITE).append(Text.literal("None").formatted(Formatting.GREEN)));
                                                    BingoManager.GameSettings.StartingGear = new ArrayList<>();
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal("stone")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting equipment set to ").formatted(Formatting.WHITE).append(Text.literal("Stone").formatted(Formatting.GREEN)));

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
                                        .then(literal("iron")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting equipment set to ").formatted(Formatting.WHITE).append(Text.literal("Iron").formatted(Formatting.GREEN)));
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
                                        .then(literal("diamond")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting equipment set to ").formatted(Formatting.WHITE).append(Text.literal("Diamond").formatted(Formatting.GREEN)));
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
                                        .then(literal("food")
                                                .then(argument("enabled", BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            var bool = BoolArgumentType.getBool(ctx, "enabled");
                                                            var str = bool ? "Food will be given with starting equipment." : "No food will be given with starting equipment.";
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), Text.literal(str).formatted(Formatting.WHITE));
                                                            BingoManager.GameSettings.StartingGear.removeIf(gear -> gear.Name.equals("minecraft:bread"));
                                                            if (bool)
                                                                BingoManager.GameSettings.StartingGear.add(new StartingItem("minecraft:bread", 64, true, false));

                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(literal("add")
                                                .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                        .then(argument("amount", IntegerArgumentType.integer(1))
                                                                .then(argument("respawn", BoolArgumentType.bool())
                                                                        .then(argument("equip", BoolArgumentType.bool())
                                                                                .executes(ctx -> {
                                                                                    var item = ItemStackArgumentType.getItemStackArgument(ctx, "item");
                                                                                    var itemName = item.asString();

                                                                                    var count = IntegerArgumentType.getInteger(ctx, "amount");
                                                                                    var onRespawn = BoolArgumentType.getBool(ctx, "respawn");
                                                                                    var equip = BoolArgumentType.getBool(ctx, "equip");

                                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                                            Text.literal("Adding item " + itemName + " | " + count + " | " + onRespawn + " | " + equip + " to equipment.").formatted(Formatting.WHITE));
                                                                                    BingoManager.GameSettings.StartingGear.add(new StartingItem(itemName, count, onRespawn, equip));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                }))))))
                                        .then(literal("remove")
                                                .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                        .executes(ctx -> {
                                                            var item = ItemStackArgumentType.getItemStackArgument(ctx, "item");
                                                            var itemName = item.asString();
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                    Text.literal("Removing item " + itemName + " from equipment.").formatted(Formatting.WHITE));
                                                            BingoManager.GameSettings.StartingGear.removeIf(gear -> gear.Name.equals(itemName));

                                                            return Command.SINGLE_SUCCESS;
                                                        }))))

                                // Set the effects to play with
                                .then(literal("effects")
                                        .then(literal("add")
                                                .then(argument("effect", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT))
                                                        .then(argument("amplifier", IntegerArgumentType.integer(0, 255))
                                                                .executes(ctx -> {
                                                                    var status = RegistryEntryArgumentType.getStatusEffect(ctx, "effect");
                                                                    var amplifier = IntegerArgumentType.getInteger(ctx, "amplifier");
                                                                    BingoManager.GameSettings.Effects.add(new StatusEffect(status.registryKey().getValue().toString(), 99999, amplifier, true));

                                                                    var text = Text.literal(status.value().getName().getString() + " added as effect with amplifier " + amplifier).formatted(Formatting.GOLD);
                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))

                                        .then(literal("remove")
                                                .then(argument("effect", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT)))
                                                .executes(ctx -> {
                                                    var status = RegistryEntryArgumentType.getStatusEffect(ctx, "effect");
                                                    var effect = BingoManager.GameSettings.Effects.removeIf(statusEffect -> statusEffect.Type.equals(status.value().getName().toString()));
                                                    if (!effect) {
                                                        var text = Text.literal(status.value().getName().getString() + " isn't in the current list of effects to remove").formatted(Formatting.RED);
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player, text);
                                                    } else {
                                                        var text = Text.literal(status.value().getName().getString() + " effect removed").formatted(Formatting.GOLD);
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                }))

                                        .then(literal("clear")
                                                .executes(ctx -> {
                                                    BingoManager.GameSettings.Effects.clear();
                                                    var text = Text.literal("All added effects cleared").formatted(Formatting.GOLD);
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set the item pools to play with
                                .then(literal("items")
                                        .then(literal("add")
                                                .then(argument("pool", StringArgumentType.greedyString())
                                                        .suggests(BingoSettingsCommand::GetItemPoolSuggestions)
                                                        .executes(ctx -> {
                                                            var poolName = StringArgumentType.getString(ctx, "pool");
                                                            GameSettings.ItemPools.add(poolName);

                                                            var text = Text.literal(poolName + " item pool added").formatted(Formatting.GOLD);
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            return Command.SINGLE_SUCCESS;
                                                        })))

                                        .then(literal("remove")
                                                .then(argument("pool", StringArgumentType.greedyString())
                                                        .suggests(BingoSettingsCommand::GetItemPoolSuggestions)
                                                        .executes(ctx -> {
                                                            var poolName = StringArgumentType.getString(ctx, "pool");
                                                            var removed = GameSettings.ItemPools.removeIf(p -> p.equals(poolName));

                                                            if (!removed) {
                                                                var text = Text.literal(poolName + " isn't in the current list of item pools").formatted(Formatting.RED);
                                                                var player = ctx.getSource().getPlayer();
                                                                if (player != null)
                                                                    MessageHelper.sendSystemMessage(player, text);
                                                            } else {
                                                                var text = Text.literal(poolName + " item pool removed").formatted(Formatting.GOLD);
                                                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            }
                                                            return Command.SINGLE_SUCCESS;
                                                        })))

                                        .then(literal("clear")
                                                .executes(ctx -> {
                                                    GameSettings.ItemPools.clear();
                                                    var text = Text.literal("All added item pools cleared").formatted(Formatting.GOLD);
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set spawn radius
                                .then(literal("radius")
                                        .then(literal("small")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting play area set to ").formatted(Formatting.WHITE).append(Text.literal("Small").formatted(Formatting.GREEN)));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.SmallRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal("medium")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting play area set to ").formatted(Formatting.WHITE).append(Text.literal("Medium").formatted(Formatting.GREEN)));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.MediumRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal("large")
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Starting play area set to ").formatted(Formatting.WHITE).append(Text.literal("Large").formatted(Formatting.GREEN)));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.LargeRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set profile dimension
                                .then(literal("dimension")
                                        .then(argument("dimension", StringArgumentType.greedyString())
                                                .suggests(BingoSettingsCommand::GetDimensionSuggestions)
                                                .executes(ctx -> {
                                                    var dimension = StringArgumentType.getString(ctx, "dimension");
                                                    var player = ctx.getSource().getPlayer();
                                                    if (!BingoMod.CONFIG.BingoDimensions.contains(dimension)) {
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player, Text.literal("%s is not a valid bingo dimension".formatted(dimension)).formatted(Formatting.RED));
                                                    } else {
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                Text.literal("Dimension set to ").formatted(Formatting.WHITE).append(dimension).formatted(Formatting.GREEN));
                                                        GameSettings.Dimension = dimension;
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set game timer
                                .then(literal("timer")
                                        .then(argument("minutes", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    if (Game.Status == GameStatus.Idle) {
                                                        var minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                                                        if (minutes == 0) {
                                                            BingoManager.GameSettings.TimeLimit = IntegerArgumentType.getInteger(ctx, "minutes");
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                    Text.literal("Game timer disabled.").formatted(Formatting.WHITE));
                                                        } else if (minutes > 0) {
                                                            BingoManager.GameSettings.TimeLimit = IntegerArgumentType.getInteger(ctx, "minutes");
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                    Text.literal("Game timer set to %s minutes.".formatted(minutes)).formatted(Formatting.WHITE));
                                                        }
                                                    } else {
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player,
                                                                    Text.literal("Can't change timer while in game.").formatted(Formatting.RED));
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }

    private static int randomizeSettings(MinecraftServer server) {
        GameSettings = BingoMod.GamePresets.get(new Random().nextInt(BingoMod.GamePresets.size()));
        MessageHelper.broadcastChat(server.getPlayerManager(), Text.literal("Game set to: ").formatted(Formatting.GREEN).append(Text.literal(GameSettings.Name).formatted(Formatting.WHITE)));

        return Command.SINGLE_SUCCESS;
    }

    public static int tellSettings(PlayerEntity player) throws CommandSyntaxException {
        var text = Text.literal("Current Settings:").formatted(Formatting.GOLD);
        player.sendMessage(text);
        text = Text.literal("Game Mode: ").formatted(Formatting.GREEN).append(Text.literal(GameSettings.GameMode).formatted(Formatting.WHITE));
        player.sendMessage(text);
        text = Text.literal("Time Limit: ").formatted(Formatting.GREEN).append(Text.literal(GameSettings.TimeLimit > 0 ? GameSettings.TimeLimit + " Minutes" : "None").formatted(Formatting.WHITE));
        player.sendMessage(text);
        text = Text.literal("Start Area Size: ").formatted(Formatting.GREEN).append(Text.literal(String.valueOf(GameSettings.TPRandomizationRadius)).formatted(Formatting.WHITE));
        player.sendMessage(text);
        if (GameSettings != null) {
            text = Text.literal("Game Profile: ").formatted(Formatting.GREEN).append(Text.literal(GameSettings.Name).formatted(Formatting.WHITE));
            player.sendMessage(text);
        }
        var server = player.getServer();
        if (server != null) {
            text = Text.literal("PVP: ").formatted(Formatting.GREEN).append(Text.literal(server.isPvpEnabled() ? "YES" : "NO").formatted(Formatting.WHITE));
            player.sendMessage(text);
        }

        /*
        if (GameSettings.StartingGear.size() > 0) {
            text = Text.literal("Starting Gear: ").formatted(Formatting.GREEN);
            player.sendMessage(text);
            for (var gear : GameSettings.StartingGear) {
                var item = Registries.ITEM.get(new Identifier(gear.Name));
                var message = "- " + gear.Amount + " " + item.getName().getString();
                if (gear.Enchantments.size() > 0) {
                    message += " - ";
                    message += String.join(",", gear.Enchantments.stream().map(value ->
                            Objects.requireNonNull(
                                    Registries.ENCHANTMENT.get(new Identifier(value.Type))).getName(value.Level).getString()).toList());
                }

                text = Text.literal(message).formatted(Formatting.WHITE);
                player.sendMessage(text);
            }
        } else {
            text = Text.literal("Starting Gear: ").formatted(Formatting.GREEN).append(Text.literal("No Starting Gear").formatted(Formatting.WHITE));
            player.sendMessage(text);
        }

        if (GameSettings.Effects.size() > 0) {
            text = Text.literal("Effects: ").formatted(Formatting.GREEN);
            player.sendMessage(text);
            for (var entry : GameSettings.Effects) {
                var effect = Registries.STATUS_EFFECT.get(new Identifier(entry.Type));
                if (effect != null) {
                    text = Text.literal("- " + effect.getName().getString() + " | " + entry.Amplifier + " | " + entry.Duration + " seconds").formatted(Formatting.WHITE);
                    player.sendMessage(text);
                }
            }
        } else {
            text = Text.literal("Effects: ").formatted(Formatting.GREEN).append(Text.literal("No Effects").formatted(Formatting.WHITE));
            player.sendMessage(text);
        }
         */
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> GetItemPoolSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var pool : BingoMod.ItemPools) {
            if (pool.Name.toLowerCase().contains(builder.getRemainingLowerCase()))
                builder.suggest(pool.Name);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> GetDimensionSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var dimension : BingoMod.CONFIG.BingoDimensions)
            builder.suggest(dimension);
        return builder.buildFuture();
    }
}
