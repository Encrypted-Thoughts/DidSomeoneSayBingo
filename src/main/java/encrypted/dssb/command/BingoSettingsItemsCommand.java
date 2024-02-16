package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static encrypted.dssb.BingoManager.Game;
import static encrypted.dssb.BingoManager.GameSettings;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsItemsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsItemsCommand = "items";
        var settingsItemsSetCommand = "set";
        var settingsItemsAddCommand = "add";
        var settingsItemsRemoveCommand = "remove";
        var settingsItemsClearCommand = "clear";
        var itemPoolArgument = "pool";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsItemsCommand)
                                        .then(literal(settingsItemsSetCommand)
                                                .then(argument(itemPoolArgument, StringArgumentType.greedyString())
                                                        .suggests(BingoSettingsItemsCommand::GetItemPoolSuggestions)
                                                        .executes(ctx -> {
                                                            var player = ctx.getSource().getPlayer();
                                                            if (Game != null && Game.Status != GameStatus.Idle) {
                                                                if (player != null)
                                                                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.item_pool_in_process"));
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            var poolName = StringArgumentType.getString(ctx, itemPoolArgument);
                                                            GameSettings.ItemPools.clear();
                                                            GameSettings.ItemPools.add(poolName);

                                                            var text = TranslationHelper.getAsText("dssb.commands.settings.items.set.pool_set", poolName);
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            return Command.SINGLE_SUCCESS;
                                                        })))

                                        .then(literal(settingsItemsAddCommand)
                                                .then(argument(itemPoolArgument, StringArgumentType.greedyString())
                                                        .suggests(BingoSettingsItemsCommand::GetItemPoolSuggestions)
                                                        .executes(ctx -> {
                                                            var player = ctx.getSource().getPlayer();
                                                            if (Game != null && Game.Status != GameStatus.Idle) {
                                                                if (player != null)
                                                                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.item_pool_in_process"));
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            var poolName = StringArgumentType.getString(ctx, itemPoolArgument);
                                                            GameSettings.ItemPools.add(poolName);

                                                            var text = TranslationHelper.getAsText("dssb.commands.settings.items.add.pool_added", poolName);
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            return Command.SINGLE_SUCCESS;
                                                        })))

                                        .then(literal(settingsItemsRemoveCommand)
                                                .then(argument(itemPoolArgument, StringArgumentType.greedyString())
                                                        .suggests(BingoSettingsItemsCommand::GetItemPoolSuggestions)
                                                        .executes(ctx -> {
                                                            var player = ctx.getSource().getPlayer();
                                                            if (Game != null && Game.Status != GameStatus.Idle) {
                                                                if (player != null)
                                                                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.item_pool_in_process"));
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            var poolName = StringArgumentType.getString(ctx, itemPoolArgument);
                                                            var removed = GameSettings.ItemPools.removeIf(p -> p.equals(poolName));

                                                            if (!removed) {
                                                                var text = TranslationHelper.getAsText("dssb.commands.settings.items.remove.invalid_pool", poolName);
                                                                if (player != null)
                                                                    MessageHelper.sendSystemMessage(player, text);
                                                            } else {
                                                                var text = TranslationHelper.getAsText("dssb.commands.settings.items.remove.pool_removed", poolName);
                                                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                            }
                                                            return Command.SINGLE_SUCCESS;
                                                        })))

                                        .then(literal(settingsItemsClearCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (Game != null && Game.Status != GameStatus.Idle) {
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.error.item_pool_in_process"));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    GameSettings.ItemPools.clear();
                                                    var text = TranslationHelper.getAsText("dssb.commands.settings.items.clear.cleared");
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }

    private static CompletableFuture<Suggestions> GetItemPoolSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var pool : BingoMod.ItemPools) {
            if (pool.Name.toLowerCase().contains(builder.getRemainingLowerCase()))
                builder.suggest(pool.Name);
        }
        return builder.buildFuture();
    }
}
