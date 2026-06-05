package encrypted.dssb.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerAdvancementAchievedCallback {
    Event<PlayerAdvancementAchievedCallback> EVENT = EventFactory.createArrayBacked(PlayerAdvancementAchievedCallback.class, (listeners) -> (player, advancement) -> {
        for (PlayerAdvancementAchievedCallback listener : listeners) {
            listener.advancementAchieved(player, advancement);
        }
    });

    void advancementAchieved(ServerPlayer player, Advancement advancement);
}
