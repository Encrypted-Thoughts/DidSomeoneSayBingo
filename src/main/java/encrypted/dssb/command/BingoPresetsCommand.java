package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;

import static encrypted.dssb.BingoManager.Game;
import static encrypted.dssb.BingoManager.GameSettings;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoPresetsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("preset")
                                .then(argument("name", StringArgumentType.greedyString())
                                        .suggests(BingoPresetsCommand::GetGameProfileSuggestions)
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (Game == null || Game.Status == GameStatus.Idle) {
                                                var profileName = StringArgumentType.getString(ctx, "name");
                                                for (var preset : BingoMod.GamePresets) {
                                                    if (preset.Name.equals(profileName)) {
                                                        GameSettings = new GamePreset(preset);
                                                        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                                Text.literal("Game preset set to ").formatted(Formatting.WHITE).append(Text.literal(profileName).formatted(Formatting.GREEN)));
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                }

                                                if (player != null)
                                                    MessageHelper.sendSystemMessage(player,
                                                            Text.literal("No game preset called %s found.".formatted(profileName)).formatted(Formatting.WHITE));
                                            } else if (player != null)
                                                MessageHelper.sendSystemMessage(player, Text.literal("Can't change preset while game in process.").formatted(Formatting.RED));

                                            return Command.SINGLE_SUCCESS;
                                        }))));
    }

    private static CompletableFuture<Suggestions> GetGameProfileSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var preset : BingoMod.GamePresets) {
            if (preset.Name.toLowerCase().contains(builder.getRemainingLowerCase()))
                builder.suggest(preset.Name);
        }
        return builder.buildFuture();
    }
}

