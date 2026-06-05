package encrypted.dssb.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class TeleportHelper {
    public static ServerPlayer teleport(ServerPlayer player, ServerLevel world, double x, double y, double z, float yaw, float pitch) throws CommandSyntaxException {
        player.stopRiding();
        if (player.isSleeping())
            player.stopSleepInBed(true, true);
        var pos = new Vec3(x, y, z);
        var velocity = new Vec3(0, 0, 0);
        return player.teleport(new TeleportTransition(world, pos, velocity, yaw, pitch, (entity) -> {}));
    }
}
