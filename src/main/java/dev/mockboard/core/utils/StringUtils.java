package dev.mockboard.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    public static String generate(int length) {
        return RandomStringUtils.secure().nextAlphanumeric(length);
    }
}
