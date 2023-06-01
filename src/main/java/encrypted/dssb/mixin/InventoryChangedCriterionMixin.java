package encrypted.dssb.mixin;

import encrypted.dssb.event.PlayerInventoryChangedCallback;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangedCriterion.class)
public class InventoryChangedCriterionMixin {
    @Inject(at = @At("HEAD"), method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V")
    private void inventoryChanged(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, CallbackInfo ci) {
        PlayerInventoryChangedCallback.EVENT.invoker().inventoryChanged(player, stack);
    }
}