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
        
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                        .then(literal("spawn")
                                .requires(source -> source.hasPermissionLevel(2))

                                // Set the spawn point location in the spawn hub
                                .then(literal("spawnPoint")
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                                    BingoMod.CONFIG.SpawnSettings.HubCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.SaveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))

                                // Set where the bingo display board should go
                                .then(literal("displayPoint")
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    var pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                                    BingoMod.CONFIG.DisplayBoardCoords = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                                                    BingoMod.CONFIG.SaveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
