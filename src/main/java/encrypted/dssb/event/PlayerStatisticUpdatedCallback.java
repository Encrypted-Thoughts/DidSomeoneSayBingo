package encrypted.dssb.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;

public interface PlayerStatisticUpdatedCallback {
    Event<PlayerStatisticUpdatedCallback> EVENT = EventFactory.createArrayBacked(PlayerStatisticUpdatedCallback.class, (listeners) -> (player, stat, value) -> {
        for (PlayerStatisticUpdatedCallback listener : listeners) {
            listener.statUpdated(player, stat, value);
        }
    });

    void statUpdated(PlayerEntity player, Stat<?> stat, int value);
}
