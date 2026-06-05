package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoManager;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.TranslationHelper;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static encrypted.dssb.BingoManager.Game;
import static encrypted.dssb.BingoManager.GameSettings;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoPresetsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var presetCommand = "preset";
        var nameArgument = "name";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(presetCommand)
                                .then(argument(nameArgument, StringArgumentType.greedyString())
                                        .suggests(BingoPresetsCommand::GetGameProfileSuggestions)
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game == null || Game.Status == GameStatus.Idle) {
                                                var profileName = StringArgumentType.getString(ctx, nameArgument);
                                                for (var preset : BingoMod.GamePresets) {
                                                    if (preset.Name.equals(profileName)) {
                                                        GameSettings = new GamePreset(preset);
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerList(),
                                                                TranslationHelper.getAsText("dssb.commands.preset.set_to").append(Component.literal(profileName)));
                                                        BingoManager.generate(player, ctx.getSource().getServer(), false);
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                }

                                                if (player != null)
                                                    MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.preset.no_preset", profileName));
                                            } else if (player != null)
                                                MessageHelper.sendSystemMessage(player, TranslationHelper.getAsText("dssb.commands.preset.game_in_progress"));

                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }

    private static CompletableFuture<Suggestions> GetGameProfileSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (var preset : BingoMod.GamePresets) {
            if (preset.Name.toLowerCase().contains(builder.getRemainingLowerCase()))
                builder.suggest(preset.Name);
        }
        return builder.buildFuture();
    }
}

