package dev.mockboard;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Env {

    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key)).orElse(defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return parse(key, defaultValue, Integer::parseInt);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return parse(key, defaultValue, Boolean::parseBoolean);
    }

    private static <T> T parse(String key, T defaultValue, Function<String, T> parser) {
        var value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        try {
            return parser.apply(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
