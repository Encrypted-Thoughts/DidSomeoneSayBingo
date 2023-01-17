package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;

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
                                            var profileName = StringArgumentType.getString(ctx, "name");
                                            for (var preset : BingoMod.GamePresets) {
                                                if (preset.Name.equals(profileName)) {
                                                    GameSettings = new GamePreset(preset);
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Game preset set to ").formatted(Formatting.WHITE).append(Text.literal(profileName).formatted(Formatting.GREEN)));
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                            }

                                            var player = ctx.getSource().getPlayer();
                                            if (player != null)
                                                MessageHelper.sendSystemMessage(player,
                                                    Text.literal("No game preset called %s found.".formatted(profileName)).formatted(Formatting.WHITE));

                                            return Command.SINGLE_SUCCESS;
                                        }))));
/*
                        .then(literal("save")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(argument("name", StringArgumentType.greedyString())
                                        .suggests(BingoProfilesCommand::GetGameProfileSuggestions)
                                        .executes(ctx -> {
                                            var profileName = StringArgumentType.getString(ctx, "name");

                                            for (var i = 0; i < BingoMod.GameProfiles.size(); i++) {
                                                if (BingoMod.GameProfiles.get(i).Name.equals(profileName)) {
                                                    BingoMod.GameProfiles.set(i, GameSettings);
                                                    GameSettings.SaveToFile(profileName);
                                                    MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(),
                                                            Text.literal("Update to game profile %s saved.".formatted(profileName)).formatted(Formatting.WHITE));
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                            }

                                            GameSettings.Name = profileName;
                                            GameSettings.SaveToFile(profileName);
                                            BingoMod.GameProfiles.add(GameSettings);
                                            var player = ctx.getSource().getPlayer();
                                            if (player != null)
                                                MessageHelper.sendSystemMessage(player,
                                                    Text.literal("Saved new game profile called %s.".formatted(profileName)).formatted(Formatting.WHITE));

                                            return Command.SINGLE_SUCCESS;
                                        }))));
 */
    }

    private static CompletableFuture<Suggestions> GetGameProfileSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var preset : BingoMod.GamePresets) {
            if (preset.Name.toLowerCase().contains(builder.getRemainingLowerCase()))
                builder.suggest(preset.Name);
        }
        return builder.buildFuture();
    }
}

