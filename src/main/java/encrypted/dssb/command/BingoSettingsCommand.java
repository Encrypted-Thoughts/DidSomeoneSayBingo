package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayer();
                                    if (player != null)
                                        return tellSettings(player);
                                    return Command.SINGLE_SUCCESS;
                                })));
    }

    public static int tellSettings(PlayerEntity player) throws CommandSyntaxException {
        var text = TranslationHelper.getAsText("dssb.commands.settings.tell.current");
        player.sendMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.game_mode").append(TranslationHelper.getAsText(GameSettings.GameMode));
        player.sendMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.time_limit").append(GameSettings.TimeLimit > 0 ? TranslationHelper.get("dssb.commands.settings.tell.time_limit.minutes", GameSettings.TimeLimit) : TranslationHelper.get("dssb.commands.settings.tell.time_limit.none"));
        player.sendMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.area_size").append(TranslationHelper.getAsText(String.valueOf(GameSettings.TPRandomizationRadius)));
        player.sendMessage(text, false);
        if (GameSettings != null) {
            text = TranslationHelper.getAsText("dssb.commands.settings.tell.profile").append(TranslationHelper.getAsText(GameSettings.Name));
            player.sendMessage(text, false);
        }
        var server = player.getServer();
        if (server != null) {
            text = TranslationHelper.getAsText("dssb.commands.settings.tell.pvp").append(TranslationHelper.getAsText(server.isPvpEnabled() ? "dssb.commands.settings.tell.pvp.yes" : "dssb.commands.settings.tell.pvp.no"));
            player.sendMessage(text, false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
