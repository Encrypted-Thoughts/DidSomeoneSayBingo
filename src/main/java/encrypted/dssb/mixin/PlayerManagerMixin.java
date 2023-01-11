package encrypted.dssb.mixin;

import encrypted.dssb.BingoManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnPointPosition()Lnet/minecraft/util/math/BlockPos;"))
    private void redirectOverworldSpawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        var server = player.getServer();
        if (server != null) {
            var world = server.getWorld(player.getSpawnPointDimension());
            var spawn = player.getSpawnPointPosition();
            if (world != null) {
                var spawnOption = PlayerEntity.findRespawnPosition(world, spawn, player.getSpawnAngle(), player.isSpawnForced(), alive);
                if (spawnOption.isEmpty())
                    BingoManager.setPlayerSpawn(player);
            }
        }
    }
}
