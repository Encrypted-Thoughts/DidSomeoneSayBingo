package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static encrypted.dssb.BingoManager.generate;
import static net.minecraft.commands.Commands.literal;

public class BingoGenerateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var generateCommand = "generate";
        var generateStartCommand =  "start";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(generateCommand)
                                .executes(ctx -> {
                                    generate(ctx.getSource().getPlayer(), ctx.getSource().getServer(), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(literal(generateStartCommand)
                                        .executes(ctx -> {
                                            generate(ctx.getSource().getPlayer(), ctx.getSource().getServer(), true);
                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }
}
