package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.server.permissions.Permissions;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoTeamsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                                                            assignRandomTeams(ctx.getSource().getServer().getPlayerList().getPlayers(), ctx.getSource().getServer().getScoreboard(), IntegerArgumentType.getInteger(ctx, numTeamsArgument));
                                                        else
                                                            MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.team.randomize.game_in_progress"));
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal(joinCommand)
                                        .then(argument(teamArgument, TeamArgument.team())
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        Game.addNewPlayer(player, TeamArgument.getTeam(ctx, teamArgument));
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal(setCommand)
                                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                        .then(argument(playerArgument, EntityArgument.player())
                                                .then(argument(teamArgument, TeamArgument.team())
                                                        .executes(ctx -> {
                                                            var player = EntityArgument.getPlayer(ctx, playerArgument);
                                                            Game.addNewPlayer(player, TeamArgument.getTeam(ctx, teamArgument));
                                                            return Command.SINGLE_SUCCESS;
                                                        }))))));
    }
}
