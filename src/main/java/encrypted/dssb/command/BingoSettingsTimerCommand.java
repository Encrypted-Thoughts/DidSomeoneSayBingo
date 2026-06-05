package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.BingoManager;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;

import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoSettingsTimerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var timerCommand = "timer";
        var minutesArgument = "minutes";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(timerCommand)
                                        .then(argument(minutesArgument, IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    if (Game.Status == GameStatus.Idle) {
                                                        var minutes = IntegerArgumentType.getInteger(ctx, minutesArgument);
                                                        if (minutes == 0) {
                                                            BingoManager.GameSettings.TimeLimit = IntegerArgumentType.getInteger(ctx, minutesArgument);
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(),
                                                                    TranslationHelper.getAsText("dssb.commands.settings.timer.disabled"));
                                                        } else if (minutes > 0) {
                                                            BingoManager.GameSettings.TimeLimit = IntegerArgumentType.getInteger(ctx, minutesArgument);
                                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(),
                                                                    TranslationHelper.getAsText("dssb.commands.settings.timer.set", minutes));
                                                        }
                                                    } else {
                                                        var player = ctx.getSource().getPlayer();
                                                        MessageHelper.sendSystemMessage(player,
                                                                TranslationHelper.getAsText("dssb.commands.settings.timer.game_in_progress"));
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
