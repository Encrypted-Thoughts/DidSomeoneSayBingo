package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.clarify;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ClarifyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var clarifyCommand = "clarify";
        var clarifyRowArgument = "row";
        var clarifyColumnArgument = "column";

        dispatcher.register(
                literal(clarifyCommand)
                        .then(argument(clarifyRowArgument, IntegerArgumentType.integer())
                                .then(argument(clarifyColumnArgument, IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            clarify(ctx.getSource(), IntegerArgumentType.getInteger(ctx, clarifyRowArgument) - 1, IntegerArgumentType.getInteger(ctx, clarifyColumnArgument) - 1);
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
