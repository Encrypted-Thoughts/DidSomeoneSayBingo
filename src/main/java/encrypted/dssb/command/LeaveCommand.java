package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static encrypted.dssb.BingoManager.BingoPlayers;
import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.server.command.CommandManager.literal;

public class LeaveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("leave")
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player != null) {
                                var scoreboard = ctx.getSource().getServer().getScoreboard();
                                scoreboard.clearPlayerTeam(player.getName().getString());
                                BingoPlayers.removeIf(p -> player.getUuid().equals(p));
                                var text = Text.literal("%s is no longer on a team!".formatted(player.getDisplayName().getString()).formatted(Formatting.GOLD));
                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                if (Game.Status != GameStatus.Idle)
                                    BingoManager.tpToBingoSpawn(player);
                            }
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
