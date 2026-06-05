package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.commands.Commands.literal;

public class BingoSettingsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    public static int tellSettings(ServerPlayer player) throws CommandSyntaxException {
        var text = TranslationHelper.getAsText("dssb.commands.settings.tell.current");
        player.sendSystemMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.game_mode").append(TranslationHelper.getAsText(GameSettings.GameMode));
        player.sendSystemMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.time_limit").append(GameSettings.TimeLimit > 0 ? TranslationHelper.get("dssb.commands.settings.tell.time_limit.minutes", GameSettings.TimeLimit) : TranslationHelper.get("dssb.commands.settings.tell.time_limit.none"));
        player.sendSystemMessage(text, false);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.area_size").append(TranslationHelper.getAsText(String.valueOf(GameSettings.TPRandomizationRadius)));
        player.sendSystemMessage(text, false);
        if (GameSettings != null) {
            text = TranslationHelper.getAsText("dssb.commands.settings.tell.profile").append(TranslationHelper.getAsText(GameSettings.Name));
            player.sendSystemMessage(text, false);
        }
        var PVPEnabled = player.level().getGameRules().get(GameRules.PVP);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.pvp").append(TranslationHelper.getAsText(PVPEnabled ? "dssb.commands.settings.tell.pvp.yes" : "dssb.commands.settings.tell.pvp.no"));
        player.sendSystemMessage(text, false);

        var keepInventoryEnabled = player.level().getGameRules().get(GameRules.KEEP_INVENTORY);
        text = TranslationHelper.getAsText("dssb.commands.settings.tell.keep_inventory").append(TranslationHelper.getAsText(keepInventoryEnabled ? "dssb.commands.settings.tell.keep_inventory.yes" : "dssb.commands.settings.tell.keep_inventory.no"));
        player.sendSystemMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }
}
