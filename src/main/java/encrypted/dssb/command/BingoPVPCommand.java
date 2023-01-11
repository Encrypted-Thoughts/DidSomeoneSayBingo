package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static encrypted.dssb.BingoManager.GameInProgress;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoPVPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("pvp")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (player != null) {
                                                if (!GameInProgress || ctx.getSource().hasPermissionLevel(2)) {
                                                    var bool = BoolArgumentType.getBool(ctx, "enabled");
                                                    ctx.getSource().getServer().setPvpEnabled(bool);
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), Text.literal(bool ? "PVP set to enabled." : "PVP disabled.").formatted(Formatting.WHITE));
                                                } else
                                                    MessageHelper.sendSystemMessage(player, Text.literal("Only Ops can change PVP status while game in progress.").formatted(Formatting.RED));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
