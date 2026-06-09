package dev.mockboard.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestUtils {

    public static String extractMockPath(String boardId, String fullPath) {
        var prefix = "/m/" + boardId;

        if (fullPath.startsWith(prefix)) {
            var path = fullPath.substring(prefix.length());
            if (path.isEmpty()) {
                return "/";
            }

            if (!path.startsWith("/")) {
                return "/" + path;
            }
            return path;
        }

        // should not happen
        return fullPath;
    }
}
