package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.Coordinates;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.server.permissions.Permissions;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BingoSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var spawnCommand = "spawn";
        var spawnPointArgument = "spawnPoint";
        var displayPointArgument = "displayPoint";
        var positionArgument = "position";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(spawnCommand)
                                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))

                                // Set the spawn point location in the spawn hub
                                .then(literal(spawnPointArgument)
                                        .then(argument(positionArgument, BlockPosArgument.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgument.getBlockPos(ctx, positionArgument);
                                                    BingoMod.CONFIG.SpawnSettings.HubCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.saveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set where the bingo display board should go
                                .then(literal(displayPointArgument)
                                        .then(argument(positionArgument, BlockPosArgument.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgument.getBlockPos(ctx, positionArgument);
                                                    BingoMod.CONFIG.DisplayBoardCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.saveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
