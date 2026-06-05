package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static encrypted.dssb.BingoManager.teamTP;
import static net.minecraft.commands.Commands.literal;

public class TeamTPCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var teamTPCommand = "teamtp";

        dispatcher.register(
                literal(teamTPCommand)
                        .executes(ctx -> {
                            teamTP(ctx.getSource().getPlayer(), ctx.getSource().getServer());
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
