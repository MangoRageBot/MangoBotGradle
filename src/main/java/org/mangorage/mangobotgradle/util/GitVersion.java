package org.mangorage.mangobotgradle.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public record GitVersion(String tag, String commits, String commit) {
    private static final GitVersion UNKNOWN = new GitVersion("0.0", "9999", "unknown");

    public static GitVersion getGitVersion() {
        try {
            var process = Runtime.getRuntime().exec("git describe --long --tags");
            try (var isr = new InputStreamReader(process.getInputStream())) {
                try (var br = new BufferedReader(isr)) {
                    var result = br.readLine().split("-");
                    return new GitVersion(
                            result[0],
                            result[1],
                            result[2]
                    );
                }
            }
        } catch (IOException ignored) {}
        return UNKNOWN;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public String getVersionAsString() {
        return getVersionAsString(false);
    }

    public String getVersionAsString(boolean includeCommit) {
        return includeCommit ? tag + "." + commits + "-" + commit : tag + "." + commits;
    }
}
