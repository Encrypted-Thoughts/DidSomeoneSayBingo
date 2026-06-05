package encrypted.dssb.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface PlayerInventoryChangedCallback {
    Event<PlayerInventoryChangedCallback> EVENT = EventFactory.createArrayBacked(PlayerInventoryChangedCallback.class, (listeners) -> (player, itemStack) -> {
        for (PlayerInventoryChangedCallback listener : listeners) {
            listener.inventoryChanged(player, itemStack);
        }
    });

    void inventoryChanged(ServerPlayer player, ItemStack itemStack);
}
