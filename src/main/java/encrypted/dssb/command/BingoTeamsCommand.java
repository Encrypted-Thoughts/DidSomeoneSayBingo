package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoTeamsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var teamCommand = "team";
        var randomizeCommand = "randomize";
        var joinCommand = "join";
        var setCommand = "set";
        var teamArgument = "team";
        var numTeamsArgument = "numTeams";
        var playerArgument = "player";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(teamCommand)
                                .then(literal(randomizeCommand)
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (player != null) {
                                                if (Game.Status == GameStatus.Idle)
                                                    randomizeTeams(ctx.getSource().getServer());
                                                else
                                                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.team.randomize.game_in_progress"));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })

                                        .then(argument(numTeamsArgument, IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null) {
                                                        if (Game.Status == GameStatus.Idle)
                                                            assignRandomTeams(ctx.getSource().getServer().getPlayerManager().getPlayerList(), ctx.getSource().getServer().getScoreboard(), IntegerArgumentType.getInteger(ctx, numTeamsArgument));
                                                        else
                                                            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.team.randomize.game_in_progress"));
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal(joinCommand)
                                        .then(argument(teamArgument, TeamArgumentType.team())
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        Game.addNewPlayer(player, TeamArgumentType.getTeam(ctx, teamArgument));
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal(setCommand)
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .then(argument(playerArgument, EntityArgumentType.player())
                                                .then(argument(teamArgument, TeamArgumentType.team())
                                                        .executes(ctx -> {
                                                            var player = EntityArgumentType.getPlayer(ctx, playerArgument);
                                                            if (player != null)
                                                                Game.addNewPlayer(player, TeamArgumentType.getTeam(ctx, teamArgument));
                                                            return Command.SINGLE_SUCCESS;
                                                        }))))));
    }
}
