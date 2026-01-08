package dev.mockboard.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    public static String minify(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        var result = new StringBuilder(json.length());
        boolean inString = false;
        boolean escaped = false;

        for (char c : json.toCharArray()) {
            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                result.append(c);
                continue;
            }

            if (inString) {
                result.append(c);
            } else if (!Character.isWhitespace(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
