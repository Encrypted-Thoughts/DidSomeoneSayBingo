package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoPVPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var pvpCommand = "pvp";
        var pvpEnabledArgument = "enabled";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(pvpCommand)
                                .then(argument(pvpEnabledArgument, BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game.Status == GameStatus.Idle || ctx.getSource().hasPermissionLevel(2)) {
                                                var bool = BoolArgumentType.getBool(ctx, pvpEnabledArgument);
                                                ctx.getSource().getServer().setPvpEnabled(bool);
                                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), TranslationHelper.getAsText(bool ? "dssb.commands.pvp.set_enabled" : "dssb.commands.pvp.set_disabled"));
                                            } else
                                                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.pvp.op_only"));
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
