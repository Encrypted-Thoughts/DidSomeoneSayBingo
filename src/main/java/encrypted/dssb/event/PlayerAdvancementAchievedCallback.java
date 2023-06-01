package encrypted.dssb.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerAdvancementAchievedCallback {
    Event<PlayerAdvancementAchievedCallback> EVENT = EventFactory.createArrayBacked(PlayerAdvancementAchievedCallback.class, (listeners) -> (player, advancement) -> {
        for (PlayerAdvancementAchievedCallback listener : listeners) {
            listener.advancementAchieved(player, advancement);
        }
    });

    void advancementAchieved(ServerPlayerEntity player, Advancement advancement);
}
