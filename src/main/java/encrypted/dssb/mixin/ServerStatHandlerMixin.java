package encrypted.dssb.mixin;

import encrypted.dssb.event.PlayerStatisticUpdatedCallback;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatsCounter.class)
public class ServerStatHandlerMixin {
    @Inject(at = @At("HEAD"), method = "setValue")
    private void advancementAchievedEvent(Player player, Stat<?> stat, int value, CallbackInfo ci) {
        PlayerStatisticUpdatedCallback.EVENT.invoker().statUpdated(player, stat, value);
    }
}