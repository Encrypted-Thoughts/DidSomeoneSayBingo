package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.gamerules.GameRules;

import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoPVPCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var pvpCommand = "pvp";
        var pvpEnabledArgument = "enabled";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(pvpCommand)
                                .then(argument(pvpEnabledArgument, BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game.Status == GameStatus.Idle || ctx.getSource().permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                                                var bool = BoolArgumentType.getBool(ctx, pvpEnabledArgument);
                                                var server = ctx.getSource().getServer();
                                                ctx.getSource().getLevel().getGameRules().set(GameRules.PVP, bool, server);
                                                MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), TranslationHelper.getAsText(bool ? "dssb.commands.pvp.set_enabled" : "dssb.commands.pvp.set_disabled"));
                                            } else
                                                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.pvp.op_only"));
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
