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
        dispatcher.register(
                literal("clarify")
                        .then(argument("row", IntegerArgumentType.integer())
                                .then(argument("column", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            clarify(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "row") - 1, IntegerArgumentType.getInteger(ctx, "column") - 1);
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
