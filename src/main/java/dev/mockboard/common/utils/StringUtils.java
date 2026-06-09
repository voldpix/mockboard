package dev.mockboard.common.utils;

import dev.mockboard.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    public static String generate(int length) {
        return RandomStringUtils.secure().nextAlphanumeric(length);
    }

    public static String removeTrailingSlash(String requestPath) {
        if (requestPath == null) return "";
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            return requestPath.substring(0, requestPath.length() - 1);
        }
        return requestPath;
    }

    public static int countWildcards(String path) {
        if (path == null) return 0;
        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == Constants.WILDCARD.charAt(0)) {
                count++;
            }
        }
        return count;
    }
}
