package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.permissions.Permissions;

import static encrypted.dssb.BingoManager.BingoPlayers;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LeaveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var leaveCommand = "leave";

        dispatcher.register(
                literal(leaveCommand)
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player != null) {
                                var scoreboard = ctx.getSource().getServer().getScoreboard();
                                scoreboard.removePlayerFromTeam(player.getName().getString());
                                BingoPlayers.removeIf(p -> player.getUUID().equals(p));
                                var text = TranslationHelper.getAsText("dssb.commands.leave.left_team", player.getDisplayName().getString());
                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), text);
                                BingoManager.tpToBingoSpawn(player);
                            }
                            return Command.SINGLE_SUCCESS;
                        })

                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(argument("player", EntityArgument.players())
                            .executes(ctx -> {
                                var players = EntityArgument.getPlayers(ctx, "player");
                                for (var player : players) {
                                    var scoreboard = ctx.getSource().getServer().getScoreboard();
                                    scoreboard.removePlayerFromTeam(player.getName().getString());
                                    BingoPlayers.removeIf(p -> player.getUUID().equals(p));
                                    var text = TranslationHelper.getAsText("dssb.commands.leave.left_team", player.getDisplayName().getString());
                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), text);
                                    BingoManager.tpToBingoSpawn(player);
                                }
                                return Command.SINGLE_SUCCESS;
                            })));
    }
}
