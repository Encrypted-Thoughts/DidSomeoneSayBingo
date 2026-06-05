package encrypted.dssb.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import encrypted.dssb.BingoManager;
import encrypted.dssb.gamemode.GameStatus;
import encrypted.dssb.util.TranslationHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.item.ItemEntity;

import static net.minecraft.commands.Commands.literal;

public class BingoGetMapCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var bingoCommand = "bingo";
        var getMapCommand = "getmap";

        dispatcher.register(
                literal(bingoCommand)
                .then(literal(getMapCommand)
                        .executes(ctx -> {
                            if (BingoManager.Game.Status != GameStatus.Idle) {
                                var player = ctx.getSource().getPlayer();
                                if (player != null && !player.getInventory().add(BingoManager.Game.getMap())) {
                                    var itemEntity = new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, BingoManager.Game.getMap());
                                    try (var world = player.level()) {
                                        world.addFreshEntity(itemEntity);
                                    }
                                    catch (Exception e) {
                                        player.sendSystemMessage(TranslationHelper.getAsText(""), false);
                                    }
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
