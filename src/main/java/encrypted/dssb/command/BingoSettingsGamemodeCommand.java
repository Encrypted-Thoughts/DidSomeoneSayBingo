package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.setGameMode;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsGamemodeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsGamemodeCommand = "gamemode";
        var settingsBingoCommand = "bingo";
        var settingsLockoutCommand = "lockout";
        var settingsHiddenCommand = "hidden";
        var settingsBlackoutCommand = "blackout";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsGamemodeCommand)
                                        .then(literal(settingsBingoCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "bingo");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsLockoutCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "lockout");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsHiddenCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "hidden");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                        .then(literal(settingsBlackoutCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "blackout");
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
