package encrypted.dssb.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldHelper {
    public static ServerLevel getWorldByName(MinecraftServer server, String worldName){
        for (var key : server.levelKeys()) {
            if (key.identifier().toString().equals(worldName))
                return server.getLevel(key);
        }
        return null;
    }

    public static ResourceKey<Level> getWorldRegistryKeyByName(MinecraftServer server, String worldName){
        for (var key : server.levelKeys()) {
            if (key.identifier().toString().equals(worldName))
                return key;
        }
        return null;
    }
}
