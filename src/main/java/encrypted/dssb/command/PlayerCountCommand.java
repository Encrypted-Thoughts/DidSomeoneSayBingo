package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class PlayerCountCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var playerCountCommand = "playercount";

        dispatcher.register(
                literal(playerCountCommand)
                        .executes(ctx -> {
                            var size = ctx.getSource().getServer().getPlayerList().getPlayers().size();
                            var text = TranslationHelper.getAsText("dssb.commands.player_count.total", size);
                            var player = ctx.getSource().getPlayer();
                            if (player != null)
                                MessageHelper.sendSystemMessage(player, text);
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
