package encrypted.dssb.util;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerHelper {
    public static String getPlayerName(PlayerEntity player){
        var displayName = player.getDisplayName();
        if (displayName == null)
            return player.getName().getString();
        return displayName.getString();
    }
}
