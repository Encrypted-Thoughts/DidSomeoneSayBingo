package encrypted.dssb.mixin;

import encrypted.dssb.event.PlayerAdvancementAchievedCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/Advancement;getRewards()Lnet/minecraft/advancement/AdvancementRewards;"), method = "grantCriterion")
    private void advancementAchievedEvent(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        PlayerAdvancementAchievedCallback.EVENT.invoker().advancementAchieved(owner, advancement);
    }
}