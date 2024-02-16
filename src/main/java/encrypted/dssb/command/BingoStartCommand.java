package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.start;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoStartCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var startCommandName = "start";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(startCommandName)
                                .executes(ctx -> {
                                    start(ctx.getSource().getPlayer(), ctx.getSource().getServer());
                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}