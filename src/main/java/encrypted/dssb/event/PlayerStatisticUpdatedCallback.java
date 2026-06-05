package encrypted.dssb.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;

public interface PlayerStatisticUpdatedCallback {
    Event<PlayerStatisticUpdatedCallback> EVENT = EventFactory.createArrayBacked(PlayerStatisticUpdatedCallback.class, (listeners) -> (player, stat, value) -> {
        for (PlayerStatisticUpdatedCallback listener : listeners) {
            listener.statUpdated(player, stat, value);
        }
    });

    void statUpdated(Player player, Stat<?> stat, int value);
}
