package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.end;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoEndCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var endCommand = "end";

        dispatcher.register(literal(bingoCommand)
                .then(literal(endCommand)
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> {
                            end(ctx.getSource().getServer());
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}