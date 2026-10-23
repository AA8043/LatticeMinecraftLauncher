package org.a8043.lmcl.game.version.manifest;

import java.util.regex.Pattern;

public final class PlatformClassifier {
    private static final Pattern PATTERN = Pattern.compile(
        "^(natives-)?(linux|windows|osx|macos)([-_](x86_64|aarch_64|arm64|x86|arm))?$");

    private PlatformClassifier() {
    }

    public static boolean isPlatform(String classifier) {
        return classifier != null && PATTERN.matcher(classifier).matches();
    }
}
