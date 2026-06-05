package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.BingoManager;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoSettingsEffectsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        var bingoCommand = "bingo";
        var settingsCommand = "settings";
        var settingsEffectsCommand = "effects";
        var settingsEffectsAddCommand = "add";
        var settingsEffectsRemoveCommand = "remove";
        var settingsEffectsClearCommand = "clear";
        var settingsEffectArgument = "effect";
        var settingsEffectAmplifierArgument = "amplifier";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(settingsCommand)
                                .then(literal(settingsEffectsCommand)
                                        .then(literal(settingsEffectsAddCommand)
                                                .then(argument(settingsEffectArgument, ResourceArgument.resource(registryAccess, Registries.MOB_EFFECT))
                                                        .then(argument(settingsEffectAmplifierArgument, IntegerArgumentType.integer(0, 255))
                                                                .executes(ctx -> {
                                                                    var status = ResourceArgument.getMobEffect(ctx, settingsEffectArgument);
                                                                    var amplifier = IntegerArgumentType.getInteger(ctx, settingsEffectAmplifierArgument);
                                                                    BingoManager.GameSettings.Effects.add(new StatusEffect(status.key().identifier().toString(), 99999, amplifier, true));

                                                                    var text = TranslationHelper.getAsText("dssb.commands.settings.effects.add.effect_added", status.value().getDisplayName().getString(), amplifier);
                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), text);
                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))

                                        .then(literal(settingsEffectsRemoveCommand)
                                                .then(argument(settingsEffectArgument, ResourceArgument.resource(registryAccess, Registries.MOB_EFFECT)))
                                                .executes(ctx -> {
                                                    var status = ResourceArgument.getMobEffect(ctx, settingsEffectArgument);
                                                    var effect = BingoManager.GameSettings.Effects.removeIf(statusEffect -> statusEffect.Type.equals(status.value().getDisplayName().toString()));
                                                    if (!effect) {
                                                        var text = TranslationHelper.getAsText("dssb.commands.settings.effects.remove.invalid_effect", status.value().getDisplayName().getString());
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player, text);
                                                    } else {
                                                        var text = TranslationHelper.getAsText("dssb.commands.settings.effects.remove.effect_removed", status.value().getDisplayName().getString());
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), text);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                }))

                                        .then(literal(settingsEffectsClearCommand)
                                                .executes(ctx -> {
                                                    BingoManager.GameSettings.Effects.clear();
                                                    var text = TranslationHelper.getAsText("dssb.commands.settings.effects.clear.cleared");
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(), text);
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
