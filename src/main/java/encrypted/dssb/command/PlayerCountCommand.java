package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class PlayerCountCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("playercount")
                        .executes(ctx -> {
                            var size = ctx.getSource().getServer().getPlayerManager().getPlayerList().size();
                            var text = Text.literal("Player Total: %s".formatted(size)).formatted(Formatting.GOLD);
                            var player = ctx.getSource().getPlayer();
                            if (player != null)
                                MessageHelper.sendSystemMessage(player, text);
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
