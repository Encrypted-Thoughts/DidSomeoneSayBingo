package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import encrypted.dssb.BingoManager;
import encrypted.dssb.config.gameprofiles.StatusEffect;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSettingsEffectsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
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
                                                .then(argument(settingsEffectArgument, RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT))
                                                        .then(argument(settingsEffectAmplifierArgument, IntegerArgumentType.integer(0, 255))
                                                                .executes(ctx -> {
                                                                    var status = RegistryEntryReferenceArgumentType.getStatusEffect(ctx, settingsEffectArgument);
                                                                    var amplifier = IntegerArgumentType.getInteger(ctx, settingsEffectAmplifierArgument);
                                                                    BingoManager.GameSettings.Effects.add(new StatusEffect(status.registryKey().getValue().toString(), 99999, amplifier, true));

                                                                    var text = TranslationHelper.getAsText("dssb.commands.settings.effects.add.effect_added", status.value().getName().getString(), amplifier);
                                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                                    return Command.SINGLE_SUCCESS;
                                                                }))))

                                        .then(literal(settingsEffectsRemoveCommand)
                                                .then(argument(settingsEffectArgument, RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT)))
                                                .executes(ctx -> {
                                                    var status = RegistryEntryReferenceArgumentType.getStatusEffect(ctx, settingsEffectArgument);
                                                    var effect = BingoManager.GameSettings.Effects.removeIf(statusEffect -> statusEffect.Type.equals(status.value().getName().toString()));
                                                    if (!effect) {
                                                        var text = TranslationHelper.getAsText("dssb.commands.settings.effects.remove.invalid_effect", status.value().getName().getString());
                                                        var player = ctx.getSource().getPlayer();
                                                        if (player != null)
                                                            MessageHelper.sendSystemMessage(player, text);
                                                    } else {
                                                        var text = TranslationHelper.getAsText("dssb.commands.settings.effects.remove.effect_removed", status.value().getName().getString());
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                }))

                                        .then(literal(settingsEffectsClearCommand)
                                                .executes(ctx -> {
                                                    BingoManager.GameSettings.Effects.clear();
                                                    var text = TranslationHelper.getAsText("dssb.commands.settings.effects.clear.cleared");
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
