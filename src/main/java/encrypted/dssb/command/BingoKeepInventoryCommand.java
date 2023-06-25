package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoKeepInventoryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("keepInventory")
                                .then(argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game.Status == GameStatus.Idle || ctx.getSource().hasPermissionLevel(2)) {
                                                var bool = BoolArgumentType.getBool(ctx, "enabled");
                                                var server = ctx.getSource().getServer();
                                                server.getGameRules().get(GameRules.KEEP_INVENTORY).set(bool, server);
                                                MessageHelper.broadcastChat(server.getPlayerManager(), Text.literal(bool ? "Keep Inventory set to enabled." : "Keep Inventory disabled.").formatted(Formatting.WHITE));
                                            } else
                                                MessageHelper.sendSystemMessage(player, Text.literal("Only Ops can change Keep Inventory status while game in progress.").formatted(Formatting.RED));
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
