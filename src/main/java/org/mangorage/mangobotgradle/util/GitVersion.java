package org.mangorage.mangobotgradle.util;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public record GitVersion(String tag, String commits, String commit) {
    private static final GitVersion UNKNOWN = new GitVersion("0.0", "9999", "unknown");

    public static GitVersion getGitVersion() {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build()) {
                try (RevWalk walk = new RevWalk(repository)) {
                    RevCommit headCommit = walk.parseCommit(repository.resolve("HEAD"));
                    String commitHash = headCommit.getName().substring(0, 7);

                    // Find the latest tag
                    String tag = repository.getRefDatabase().getRefsByPrefix("refs/tags/")
                            .stream()
                            .map(ref -> ref.getName().substring("refs/tags/".length()))
                            .reduce((first, second) -> second) // Get the latest tag
                            .orElse("");

                    if (tag.isBlank())
                        return UNKNOWN;

                    // Count commits since the tag
                    int commitCount = 0;
                    walk.markStart(headCommit);
                    for (RevCommit commit : walk) {
                        commitCount++;
                    }

                    return new GitVersion(tag, String.valueOf(commitCount), commitHash);
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
