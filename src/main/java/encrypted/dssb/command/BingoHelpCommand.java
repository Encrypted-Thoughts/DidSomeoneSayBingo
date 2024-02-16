package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class BingoHelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";

        dispatcher.register(
                literal(bingoCommand).executes(ctx -> {
                    var player = ctx.getSource().getPlayer();
                    if (player != null)
                        return execute(ctx.getSource().getPlayer());
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static int execute(ServerPlayerEntity player) {
        var isOp = player.hasPermissionLevel(2);
        MessageHelper.sendSystemMessage(player, Text.translatable("dssb.commands.help.available_commands"));
        sendHelpMessage(player, "dssb.commands.help.generate_description", false);
        sendHelpMessage(player, "dssb.commands.help.start_description", false);
        if (isOp) sendHelpMessage(player, "dssb.commands.help.end_description", true);
        sendHelpMessage(player, "dssb.commands.help.preset_description", false);
        sendHelpMessage(player, "dssb.commands.help.settings_description", false);
        sendHelpMessage(player, "dssb.commands.help.settings_gamemode_description", false);
        sendHelpMessage(player, "dssb.commands.help.settings_equipment_description", false);
        sendHelpMessage(player, "dssb.commands.help.settings_effects_description", false);
        sendHelpMessage(player, "dssb.commands.help.settings_timer_description", false);
        if (isOp) sendHelpMessage(player, "dssb.commands.help.settings_dimension_description", true);
        if (isOp) sendHelpMessage(player, "dssb.commands.help.settings_spawn_point_description", true);
        if (isOp) sendHelpMessage(player, "dssb.commands.help.settings_display_point_description", true);
        sendHelpMessage(player, "dssb.commands.help.pvp_description", false);
        sendHelpMessage(player, "dssb.commands.help.get_map_description", false);
        sendHelpMessage(player,  "dssb.commands.help.vote_end_description", false);
        sendHelpMessage(player,  "dssb.commands.help.team_randomize_description", false);
        sendHelpMessage(player,  "dssb.commands.help.team_join_description", false);
        if (isOp) sendHelpMessage(player, "dssb.commands.help.team_set_description", true);
        sendHelpMessage(player, "dssb.commands.help.clarify_description", false);
        sendHelpMessage(player, "dssb.commands.help.team_tp_description", false);
        sendHelpMessage(player, "dssb.commands.help.leave_description", false);

        return Command.SINGLE_SUCCESS;
    }

    private static void sendHelpMessage(ServerPlayerEntity player, String descriptionId, boolean isOp){
        var descriptionText = TranslationHelper.getAsText(descriptionId);

        if (isOp) {
            var opText = TranslationHelper.getAsText("dssb.commands.help.op_only");
            MessageHelper.sendSystemMessage(player, opText.append(descriptionText));
        }
        else
            MessageHelper.sendSystemMessage(player, descriptionText);
    }
}
