package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permissions;

import static encrypted.dssb.BingoManager.end;
import static net.minecraft.commands.Commands.literal;

public class BingoEndCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var endCommand = "end";

        dispatcher.register(literal(bingoCommand)
                .then(literal(endCommand)
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(ctx -> {
                            end(ctx.getSource().getServer());
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}