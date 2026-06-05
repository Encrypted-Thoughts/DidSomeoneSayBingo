package encrypted.dssb.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TranslationHelper {
    public static MutableComponent getAsText(String key) {
        return Component.translatable(key);
    }

    public static MutableComponent getAsText(String key, Object... args) {
        return Component.literal(get(key, args));
    }

    public static String get(String key) {
        return getAsText(key).getString();
    }

    public static String get(String key, Object... args) {
        return Component.translatable(key).getString().formatted(args);
    }
}
