package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.util.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class BingoHelpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand).executes(ctx -> {
                    var player = ctx.getSource().getPlayer();
                    if (player != null)
                        return execute(ctx.getSource().getPlayer());
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static int execute(ServerPlayerEntity player) {
        var isOp = player.hasPermissionLevel(2);
        MessageHelper.sendSystemMessage(player, Text.literal("Available Commands:").formatted(Formatting.GOLD));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo generate <start>: ").formatted(Formatting.GREEN).append(Text.literal("Generates a new bingo card and optionally starts a game at the same time.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo start: ").formatted(Formatting.GREEN).append(Text.literal("Starts a game of bingo with the current generated card.").formatted(Formatting.WHITE)));
        if (isOp) MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo end: ").formatted(Formatting.GREEN).append(Text.literal("Command to forcefully end the current game of bingo.").formatted(Formatting.WHITE))));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo profile <Easy|Normal|Hard|Nether>: ").formatted(Formatting.GREEN).append(Text.literal("Set the item pool to be used for the game.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo settings: ").formatted(Formatting.GREEN).append(Text.literal("View current bingo settings.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo settings gamemode <Bingo|Lockout|Blackout>: ").formatted(Formatting.GREEN).append(Text.literal("Set the game mode type to play. Bingo: just normal bingo. Lockout: Getting an item locks it out from other teams. Win on bingo or getting the majority of items. Blackout: Get all the items to win.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo settings equipment <None|Stone|Iron|Diamond|Food>: ").formatted(Formatting.GREEN).append(Text.literal("Set the type of starting equipment to begin the game with.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo settings effects <add|remove|clear>: ").formatted(Formatting.GREEN).append(Text.literal("Set status effects that should be applied during the game (blindness, speed, restoration, etc)").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo settings timer <minutes>: ").formatted(Formatting.GREEN).append(Text.literal("Set the time limit in minutes for the game.").formatted(Formatting.WHITE)));
        if (isOp) {
            MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo settings dimension <dimension>: ").formatted(Formatting.GREEN).append(Text.literal("Set the dimension the game is to be started in.").formatted(Formatting.WHITE))));
            MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo spawn spawnPoint <coords>: ").formatted(Formatting.GREEN).append(Text.literal("Set the spawn point for the bingo spawn hub for players to be teleported to after games.").formatted(Formatting.WHITE))));
            MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo spawn displayPoint <coords>: ").formatted(Formatting.GREEN).append(Text.literal("Set the bottom left location of where the bingo display board should be located.").formatted(Formatting.WHITE))));
            MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo spawn concrete <start|stop|block>: ").formatted(Formatting.GREEN).append(Text.literal("Commands for setting the concrete blocks to be changed to the victors colors in the spawn hub.").formatted(Formatting.WHITE))));
            MessageHelper.sendSystemMessage(player, Text.literal("(Op only) ").formatted(Formatting.DARK_RED).append(Text.literal("/bingo spawn glass <start|stop|block>: ").formatted(Formatting.GREEN).append(Text.literal("Commands for setting the glass blocks to be changed to the victors colors in the spawn hub.").formatted(Formatting.WHITE))));
        }
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo pvp <true|false>: ").formatted(Formatting.GREEN).append(Text.literal("Set whether pvp is enabled or disabled.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo getmap: ").formatted(Formatting.GREEN).append(Text.literal("Get a copy of the map for the bingo card.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo voteend: ").formatted(Formatting.GREEN).append(Text.literal("Start a vote to end the current game of bingo or if a vote is in progress cast a vote to end.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo team randomize <numTeams>: ").formatted(Formatting.GREEN).append(Text.literal("Assign all players randomly to teams.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/bingo team join <team>: ").formatted(Formatting.GREEN).append(Text.literal("Join the specified team.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/clarify <row> <col>: ").formatted(Formatting.GREEN).append(Text.literal("Get the name of the item on the bingo card at the specified row and column.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/teamtp: ").formatted(Formatting.GREEN).append(Text.literal("teleport to a random teammate that's more than 50 blocks away.").formatted(Formatting.WHITE)));
        MessageHelper.sendSystemMessage(player, Text.literal("/leave: ").formatted(Formatting.GREEN).append(Text.literal("Leave your current team and become teamless.").formatted(Formatting.WHITE)));

        return Command.SINGLE_SUCCESS;
    }
}
