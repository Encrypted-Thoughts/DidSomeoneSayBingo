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
        var settingsBlackoutCommand = "blackout";
        var settingsHiddenCommand = "hidden";

        var settingsHiddenDiagonalCommand = "diagonal";
        var settingsHiddenDoubleDiagonalCommand = "doubleDiagonal";
        var settingsHiddenCheckerboardCommand = "checkerboard";
        var settingsHiddenInverseCheckerboardCommand = "inverseCheckerboard";
        var settingsHiddenAllCommand = "all";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsGamemodeCommand)
                                        .then(literal(settingsBingoCommand)
                                            .executes(ctx -> {
                                                var player = ctx.getSource().getPlayer();
                                                if (player != null)
                                                    setGameMode(player, ctx.getSource().getServer(), "bingo", null);
                                                return Command.SINGLE_SUCCESS;
                                            }))
                                        .then(literal(settingsLockoutCommand)
                                            .executes(ctx -> {
                                                var player = ctx.getSource().getPlayer();
                                                if (player != null)
                                                    setGameMode(player, ctx.getSource().getServer(), "lockout", null);
                                                return Command.SINGLE_SUCCESS;
                                            }))
                                        .then(literal(settingsHiddenCommand)
                                            .then(literal(settingsHiddenDiagonalCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "hidden", "diagonal");
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                            .then(literal(settingsHiddenDoubleDiagonalCommand)
                                                    .executes(ctx -> {
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            setGameMode(player, ctx.getSource().getServer(), "hidden", "doubleDiagonal");
                                                        return Command.SINGLE_SUCCESS;
                                                    }))
                                            .then(literal(settingsHiddenCheckerboardCommand)
                                                    .executes(ctx -> {
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            setGameMode(player, ctx.getSource().getServer(), "hidden", "checkerboard");
                                                        return Command.SINGLE_SUCCESS;
                                                    }))
                                            .then(literal(settingsHiddenInverseCheckerboardCommand)
                                                    .executes(ctx -> {
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            setGameMode(player, ctx.getSource().getServer(), "hidden", "inverseCheckerboard");
                                                        return Command.SINGLE_SUCCESS;
                                                    }))
                                            .then(literal(settingsHiddenAllCommand)
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setGameMode(player, ctx.getSource().getServer(), "hidden", "all");
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                                        .then(literal(settingsBlackoutCommand)
                                            .executes(ctx -> {
                                                var player = ctx.getSource().getPlayer();
                                                if (player != null)
                                                    setGameMode(player, ctx.getSource().getServer(), "blackout", null);
                                                return Command.SINGLE_SUCCESS;
                                            })))));
    }
}
