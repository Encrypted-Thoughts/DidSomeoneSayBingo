package encrypted.dssb.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;

public class TeleportHelper {
    public static void teleport(ServerPlayerEntity player, ServerWorld world, double x, double y, double z, float yaw, float pitch) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!World.isValid(blockPos))
            throw new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition")).create();
        else {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
            player.stopRiding();
            if (player.isSleeping())
                player.wakeUp(true, true);
            if (world == player.world)
                player.networkHandler.requestTeleport(x, y, z, f, g, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
            else
                player.teleport(world, x, y, z, f, g);
            player.setHeadYaw(f);

            if (!player.isFallFlying()) {
                player.setVelocity(player.getVelocity().multiply(1.0, 0.0, 1.0));
                player.setOnGround(true);
            }
        }
    }
}
