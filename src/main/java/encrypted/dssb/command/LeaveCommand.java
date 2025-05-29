package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.BingoPlayers;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LeaveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var leaveCommand = "leave";

        dispatcher.register(
                literal(leaveCommand)
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player != null) {
                                var scoreboard = ctx.getSource().getServer().getScoreboard();
                                scoreboard.clearTeam(player.getName().getString());
                                BingoPlayers.removeIf(p -> player.getUuid().equals(p));
                                var text = TranslationHelper.getAsText("dssb.commands.leave.left_team", player.getDisplayName().getString());
                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                BingoManager.tpToBingoSpawn(player);
                            }
                            return Command.SINGLE_SUCCESS;
                        })

                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("player", EntityArgumentType.players())
                                .executes(ctx -> {
                                    var players = EntityArgumentType.getPlayers(ctx, "player");
                                    for (var player : players) {
                                        if (player != null) {
                                            var scoreboard = ctx.getSource().getServer().getScoreboard();
                                            scoreboard.clearTeam(player.getName().getString());
                                            BingoPlayers.removeIf(p -> player.getUuid().equals(p));
                                            var text = TranslationHelper.getAsText("dssb.commands.leave.left_team", player.getDisplayName().getString());
                                            MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                            BingoManager.tpToBingoSpawn(player);
                                        }
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}
