package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.ServerCommandSource;

import static encrypted.dssb.BingoManager.*;
import static encrypted.dssb.BingoManager.Game;
import static net.minecraft.server.command.CommandManager.literal;

public class BingoGetMapCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal(BingoCommands.bingoCommand)
                .then(literal("getmap")
                        .executes(ctx -> {
                            if (GameInProgress) {
                                var player = ctx.getSource().getPlayer();
                                if (player != null && !player.getInventory().insertStack(Game.Card.getMap())) {
                                    var itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, Game.Card.getMap());
                                    player.world.spawnEntity(itemEntity);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
