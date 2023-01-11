package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import encrypted.dssb.BingoManager;
import encrypted.dssb.util.MessageHelper;
import encrypted.dssb.util.WorldHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import static encrypted.dssb.BingoManager.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoTeamsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("team")
                                .then(literal("randomize")
                                        .executes(ctx -> {
                                            var player = ctx.getSource().getPlayer();
                                            if (player != null) {
                                                if (!GameInProgress)
                                                    randomizeTeams(ctx.getSource().getServer());
                                                else
                                                    MessageHelper.sendSystemMessage(player, Text.literal("Can't randomize teams while game in progress.").formatted(Formatting.RED));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })

                                        .then(argument("numTeams", IntegerArgumentType.integer())
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null) {
                                                        if (!GameInProgress)
                                                            assignRandomTeams(ctx.getSource().getServer().getPlayerManager().getPlayerList(), ctx.getSource().getServer().getScoreboard(), IntegerArgumentType.getInteger(ctx, "numTeams"));
                                                        else
                                                            MessageHelper.sendSystemMessage(player, Text.literal("Can't randomize teams while game in progress.").formatted(Formatting.RED));
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal("join")
                                        .then(argument("team", TeamArgumentType.team())
                                                .executes(ctx -> {
                                                    var player = ctx.getSource().getPlayer();
                                                    if (player != null)
                                                        setPlayerTeam(ctx, player);
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                .then(literal("set")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .then(argument("player", EntityArgumentType.player())
                                                .then(argument("team", TeamArgumentType.team())
                                                        .executes(ctx -> {
                                                            var player = EntityArgumentType.getPlayer(ctx, "player");
                                                            if (player != null)
                                                                setPlayerTeam(ctx, player);
                                                            return Command.SINGLE_SUCCESS;
                                                        }))))));
    }

    private static void setPlayerTeam(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        var scoreboard = ctx.getSource().getServer().getScoreboard();
        var team = TeamArgumentType.getTeam(ctx, "team");
        scoreboard.addPlayerToTeam(player.getName().getString(), team);
        if (!BingoPlayers.contains(player.getUuid()))
            BingoPlayers.add(player.getUuid());
        var text = Text.literal("%s joined team %s!".formatted(player.getDisplayName().getString(), team.getName())).formatted(team.getColor());
        MessageHelper.broadcastChat(ctx.getSource().getServer().getPlayerManager(), text);
        if (GameInProgress) {
            var server = player.getServer();
            if (server != null) {
                BingoManager.givePlayerStatusEffects(player, true);
                BingoManager.givePlayerEquipment(player, true);
                BingoManager.Game.teleportPlayerToTeamSpawn(
                        WorldHelper.getWorldByName(server, BingoManager.GameSettings.Dimension),
                        player,
                        Game.TeamSpawns.get(team).offset(Direction.Axis.Y, Game.ySpawnOffset)
                );
            }
        }
    }
}
