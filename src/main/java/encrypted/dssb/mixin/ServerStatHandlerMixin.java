package encrypted.dssb.mixin;

import encrypted.dssb.event.PlayerStatisticUpdatedCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatHandler.class)
public class ServerStatHandlerMixin {
    @Inject(at = @At("HEAD"), method = "setStat")
    private void advancementAchievedEvent(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        PlayerStatisticUpdatedCallback.EVENT.invoker().statUpdated(player, stat, value);
    }
}