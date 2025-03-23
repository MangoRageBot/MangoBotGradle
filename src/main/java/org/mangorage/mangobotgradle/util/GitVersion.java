package org.mangorage.mangobotgradle.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public record GitVersion(String tag, String commits, String commit, String lastCommitMessage) {
    private static final GitVersion UNKNOWN = new GitVersion("0.0", "9999", "unknown", "unknown");

    public static GitVersion getGitVersion() {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();
                 Git git = new Git(repository)) {

                // Find latest tag
                List<RefSpec> tagRefs = git.tagList().call().stream()
                        .map(ref -> new RefSpec(ref.getName()))
                        .toList();

                Optional<String> latestTag = tagRefs.stream()
                        .map(ref -> ref.getSource().substring("refs/tags/".length()))
                        .reduce((first, second) -> second); // Get the latest tag

                if (latestTag.isEmpty()) return UNKNOWN; // No tags found

                String tagName = latestTag.get();
                RevCommit taggedCommit = findTaggedCommit(repository, tagName);

                // Count commits since the latest tag
                int commitCount = countCommitsSince(repository, taggedCommit);

                // Get HEAD commit short hash
                String commitHash = repository.resolve("HEAD").getName().substring(0, 7);

                // Get last commit message
                String lastCommitMessage = getLastCommitMessage(repository);

                return new GitVersion(tagName, String.valueOf(commitCount), commitHash, lastCommitMessage);
            }
        } catch (Exception ignored) {}
        return UNKNOWN;
    }

    private static RevCommit findTaggedCommit(Repository repository, String tagName) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            return walk.parseCommit(repository.resolve("refs/tags/" + tagName));
        }
    }

    private static int countCommitsSince(Repository repository, RevCommit taggedCommit) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit headCommit = walk.parseCommit(repository.resolve("HEAD"));
            walk.markStart(headCommit);

            int count = 0;
            for (RevCommit commit : walk) {
                if (commit.equals(taggedCommit)) break; // Stop at the tagged commit
                count++;
            }
            return count;
        }
    }

    private static String getLastCommitMessage(Repository repository) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit headCommit = walk.parseCommit(repository.resolve("HEAD"));
            return headCommit.getShortMessage();
        }
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

    public String getLastCommitMessage() {
        return lastCommitMessage;
    }
}
