package encrypted.dssb.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TranslationHelper {
    public static MutableText getAsText(String key) {
        return Text.translatable(key);
    }

    public static MutableText getAsText(String key, Object... args) {
        return Text.literal(get(key, args));
    }

    public static String get(String key) {
        return getAsText(key).getString();
    }

    public static String get(String key, Object... args) {
        return Text.translatable(key).getString().formatted(args);
    }
}
