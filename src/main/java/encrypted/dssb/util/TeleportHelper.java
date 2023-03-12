package encrypted.dssb.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;

public class TeleportHelper {
    public static void teleport(ServerPlayerEntity player, ServerWorld world, double x, double y, double z, float yaw, float pitch) throws CommandSyntaxException {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        if (!World.isValid(blockPos))
            throw new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition")).create();
        else {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            player.stopRiding();
            if (player.isSleeping())
                player.wakeUp(true, true);

            if (player.teleport(world, x, y, z, EnumSet.noneOf(PositionFlag.class), f, g)) {
                player.setHeadYaw(f);
                if (player.isFallFlying()) return;
                player.setVelocity(player.getVelocity().multiply(1.0, 0.0, 1.0));
                player.setOnGround(true);
            }
        }
    }
}
