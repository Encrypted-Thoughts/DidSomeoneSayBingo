package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.gamemode.GameStatus;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BingoGetMapCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var bingoCommand = "bingo";
        var getMapCommand = "getmap";

        dispatcher.register(
                literal(bingoCommand)
                .then(literal(getMapCommand)
                        .executes(ctx -> {
                            if (BingoManager.Game.Status != GameStatus.Idle) {
                                var player = ctx.getSource().getPlayer();
                                if (player != null && !player.getInventory().insertStack(BingoManager.Game.getMap())) {
                                    var itemEntity = new ItemEntity(player.getEntityWorld(), player.getEntityPos().x, player.getEntityPos().y, player.getEntityPos().z, BingoManager.Game.getMap());
                                    player.getEntityWorld().spawnEntity(itemEntity);
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
