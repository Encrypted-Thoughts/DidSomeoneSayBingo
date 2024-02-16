package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoKeepInventoryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var keepInventoryCommand = "keepInventory";
        var keepInventoryEnabledArgument = "enabled";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(keepInventoryCommand)
                                .then(argument(keepInventoryEnabledArgument, BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game.Status == GameStatus.Idle || ctx.getSource().hasPermissionLevel(2)) {
                                                var bool = BoolArgumentType.getBool(ctx, keepInventoryEnabledArgument);
                                                var server = ctx.getSource().getServer();
                                                server.getGameRules().get(GameRules.KEEP_INVENTORY).set(bool, server);
                                                MessageHelper.broadcastChat(server.getPlayerManager(), TranslationHelper.getAsText(bool ? "dssb.commands.keep_inventory.set_enabled" : "dssb.commands.keep_inventory.set_disabled"));
                                            } else
                                                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.keep_inventory.op_only"));
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
