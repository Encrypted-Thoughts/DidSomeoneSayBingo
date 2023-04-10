package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.teamTP;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamTPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("teamtp")
                        .executes(ctx -> {
                            teamTP(ctx.getSource().getPlayer(), ctx.getSource().getServer());
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
