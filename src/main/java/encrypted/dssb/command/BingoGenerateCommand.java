package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.generate;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoGenerateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
