package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.Coordinates;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var spawnCommand = "spawn";
        var spawnPointArgument = "spawnPoint";
        var displayPointArgument = "displayPoint";
        var positionArgument = "position";

        dispatcher.register(
                literal(bingoCommand)
                        .then(literal(spawnCommand)
                                .requires(source -> source.hasPermissionLevel(2))

                                // Set the spawn point location in the spawn hub
                                .then(literal(spawnPointArgument)
                                        .then(argument(displayPointArgument, BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgumentType.getBlockPos(ctx, positionArgument);
                                                    BingoMod.CONFIG.SpawnSettings.HubCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.saveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set where the bingo display board should go
                                .then(literal(displayPointArgument)
                                        .then(argument(positionArgument, BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgumentType.getBlockPos(ctx, positionArgument);
                                                    BingoMod.CONFIG.DisplayBoardCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.saveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
