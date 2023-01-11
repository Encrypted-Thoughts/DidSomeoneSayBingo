package encrypted.dssb.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class WorldHelper {
    public static ServerWorld getWorldByName(MinecraftServer server, String worldName){
        for (var key : server.getWorldRegistryKeys()) {
            if (key.getValue().toString().equals(worldName))
                return server.getWorld(key);
        }
        return null;
    }

    public static RegistryKey<World> getWorldRegistryKeyByName(MinecraftServer server, String worldName){
        for (var key : server.getWorldRegistryKeys()) {
            if (key.getValue().toString().equals(worldName))
                return key;
        }
        return null;
    }
}
