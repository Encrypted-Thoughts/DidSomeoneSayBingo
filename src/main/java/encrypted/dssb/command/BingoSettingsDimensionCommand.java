package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;

import static encrypted.dssb.BingoManager.Game;
import static encrypted.dssb.BingoManager.GameSettings;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoSettingsDimensionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsDimensionCommand = "dimension";
        var settingsMaxYLevelArgument = "maxYLevel";
        var settingsYSpawnOffsetArgument = "ySpawnOffset";
        var settingsDimensionArgument = "dimension";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsDimensionCommand)
                                        .then(argument(settingsDimensionArgument, DimensionArgument.dimension())
                                                .suggests(BingoSettingsDimensionCommand::GetDimensionSuggestions)
                                                .then(argument(settingsMaxYLevelArgument, IntegerArgumentType.integer())
                                                        .then(argument(settingsYSpawnOffsetArgument, IntegerArgumentType.integer())
                                                                .executes(ctx -> {
                                                                    var player = ctx.getSource().getPlayer();
                                                                    GameSettings.MaxYLevel = IntegerArgumentType.getInteger(ctx, settingsMaxYLevelArgument);
                                                                    GameSettings.YSpawnOffset = IntegerArgumentType.getInteger(ctx, settingsYSpawnOffsetArgument);

                                                                    if (Game != null && Game.Status != GameStatus.Idle) {
                                                                        MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.settings.dimension.game_in_process"));
                                                                        return Command.SINGLE_SUCCESS;
                                                                    }

                                                                    var dimension = DimensionArgument.getDimension(ctx, settingsDimensionArgument);
                                                                    var dimensionName = dimension.dimension().identifier().toString();
                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(),
                                                                            TranslationHelper.getAsText("dssb.commands.settings.dimension.set_to").append(dimensionName));
                                                                    GameSettings.Dimension = dimensionName;

                                                                    return Command.SINGLE_SUCCESS;
                                                                })))))));
    }

    private static CompletableFuture<Suggestions> GetDimensionSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (var dimension : BingoMod.CONFIG.BingoDimensions)
            builder.suggest(dimension);
        return builder.buildFuture();
    }
}
