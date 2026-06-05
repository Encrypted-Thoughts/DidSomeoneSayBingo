package encrypted.dssb.util;

import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static String getPlayerName(Player player){
        return player.getDisplayName().getString();
    }
}
