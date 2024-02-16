package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsSpawnRadiusCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var radiusCommand = "radius";
        var smallCommand = "small";
        var mediumCommand = "medium";
        var largeCommand = "large";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(radiusCommand)
                                        .then(literal(smallCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.radius.response").append(TranslationHelper.getAsText("dssb.commands.settings.radius.small")));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.SmallRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(mediumCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.radius.response").append(TranslationHelper.getAsText("dssb.commands.settings.radius.medium")));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.MediumRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(largeCommand)
                                                .executes(ctx -> {
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            TranslationHelper.getAsText("dssb.commands.settings.radius.response").append(TranslationHelper.getAsText("dssb.commands.settings.radius.large")));
                                                    BingoManager.GameSettings.TPRandomizationRadius = BingoMod.CONFIG.TPRandomizationSizes.LargeRadius;
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
