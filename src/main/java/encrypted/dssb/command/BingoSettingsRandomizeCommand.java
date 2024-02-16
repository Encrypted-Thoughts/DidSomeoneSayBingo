package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoMod;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Random;

import static encrypted.dssb.BingoManager.GameSettings;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsRandomizeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsRandomizeCommand = "randomize";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsRandomizeCommand)
                                        .executes(ctx -> randomizeSettings(ctx.getSource().getServer())))));
    }

    private static int randomizeSettings(MinecraftServer server) {
        GameSettings = BingoMod.GamePresets.get(new Random().nextInt(BingoMod.GamePresets.size()));
        MessageHelper.broadcastChat(server.getPlayerManager(), TranslationHelper.getAsText("dssb.commands.settings.randomize.game_set").append(TranslationHelper.getAsText(GameSettings.Name)));

        return Command.SINGLE_SUCCESS;
    }
}
