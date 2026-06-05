package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static encrypted.dssb.BingoManager.start;
import static net.minecraft.commands.Commands.literal;

public class BingoStartCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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