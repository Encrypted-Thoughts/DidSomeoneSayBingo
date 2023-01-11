package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoMod;
import encrypted.dssb.config.replaceblocks.Coordinates;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoSpawnCommand {
    public static BlockPos ConcreteStartPos;
    public static BlockPos GlassStartPos;

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
                                                })))

                                // Options for replacing blocks in the spawn hub on game end
                                .then(literal("concrete")
                                        .then(literal("start")
                                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                                        .executes(ctx -> {
                                                            ConcreteStartPos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(literal("stop")
                                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                                        .executes(ctx -> {
                                                            BingoMod.REPLACEMENT_BLOCKS.addBlocks(
                                                                    ConcreteStartPos,
                                                                    BlockPosArgumentType.getBlockPos(ctx, "pos"),
                                                                    "black_concrete",
                                                                    "red_concrete",
                                                                    "lime_concrete",
                                                                    "blue_concrete",
                                                                    "purple_concrete",
                                                                    "pink_concrete",
                                                                    "orange_concrete",
                                                                    "yellow_concrete",
                                                                    "cyan_concrete");
                                                            BingoMod.REPLACEMENT_BLOCKS.SaveToFile();
                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(argument("block", BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    BingoMod.REPLACEMENT_BLOCKS.addBlocks(
                                                            BlockPosArgumentType.getBlockPos(ctx, "block"),
                                                            BlockPosArgumentType.getBlockPos(ctx, "block"),
                                                            "black_concrete",
                                                            "red_concrete",
                                                            "lime_concrete",
                                                            "blue_concrete",
                                                            "purple_concrete",
                                                            "pink_concrete",
                                                            "orange_concrete",
                                                            "yellow_concrete",
                                                            "cyan_concrete");
                                                    BingoMod.REPLACEMENT_BLOCKS.SaveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                                .then(literal("glass")
                                        .then(literal("start")
                                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                                        .executes(ctx -> {
                                                            GlassStartPos = BlockPosArgumentType.getBlockPos(ctx, "pos");
                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(literal("stop")
                                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                                        .executes(ctx -> {
                                                            BingoMod.REPLACEMENT_BLOCKS.addBlocks(
                                                                    GlassStartPos,
                                                                    BlockPosArgumentType.getBlockPos(ctx, "pos"),
                                                                    "black_stained_glass",
                                                                    "red_stained_glass",
                                                                    "lime_stained_glass",
                                                                    "blue_stained_glass",
                                                                    "purple_stained_glass",
                                                                    "pink_stained_glass",
                                                                    "orange_stained_glass",
                                                                    "yellow_stained_glass",
                                                                    "cyan_stained_glass");
                                                            BingoMod.REPLACEMENT_BLOCKS.SaveToFile();
                                                            return Command.SINGLE_SUCCESS;
                                                        })))
                                        .then(argument("block", BlockPosArgumentType.blockPos())
                                                .executes(ctx -> {
                                                    BingoMod.REPLACEMENT_BLOCKS.addBlocks(
                                                            BlockPosArgumentType.getBlockPos(ctx, "block"),
                                                            BlockPosArgumentType.getBlockPos(ctx, "block"),
                                                            "black_stained_glass",
                                                            "red_stained_glass",
                                                            "lime_stained_glass",
                                                            "blue_stained_glass",
                                                            "purple_stained_glass",
                                                            "pink_stained_glass",
                                                            "orange_stained_glass",
                                                            "yellow_stained_glass",
                                                            "cyan_stained_glass");
                                                    BingoMod.REPLACEMENT_BLOCKS.SaveToFile();
                                                    return Command.SINGLE_SUCCESS;
                                                })))));
    }
}
