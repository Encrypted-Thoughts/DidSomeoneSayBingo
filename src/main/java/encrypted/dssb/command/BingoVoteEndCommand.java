package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BingoVoteEndCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("voteend")
                                .executes(ctx -> {
                                    BingoManager.handleVote(ctx.getSource().getPlayer(), ctx.getSource().getServer());
                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}
