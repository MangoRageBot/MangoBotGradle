/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobotgradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mangorage.mangobotgradle.Config;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public abstract class SetupPluginsTask extends DefaultTask {
    private final File file;

    @Inject
    public SetupPluginsTask(Config config, String group) {
        this.file = config.getJarTask().getArchiveFile().get().getAsFile();
        setGroup(group);
        setDescription("sets up the plugins");

        var dependency = config.getJarTask();

        dependsOn(dependency);
        mustRunAfter(dependency);
    }

    @TaskAction
    public void run() {
        Path plugins = getProject().getProjectDir().toPath().resolve("build/run/plugins");

        try (final var files = Files.list(plugins)){
            files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .forEach(path -> {
                        try {
                            System.out.println("Deleted -> " + path);
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.copy(file.toPath(), plugins.resolve(file.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getProject().getConfigurations().getByName("bootstrap").getFiles().forEach(a -> {
            try {
                if (!Files.exists(getProject().getProjectDir().toPath().resolve("build/run/boot/")))
                    Files.createDirectories(getProject().getProjectDir().toPath().resolve("build/run/boot/"));

                Files.copy(a.toPath(), getProject().getProjectDir().toPath().resolve("build/run/boot/boot.jar"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        getProject().getConfigurations().getByName("plugin").getFiles().forEach(file -> {
            try {
                Files.copy(file.toPath(), plugins.resolve(file.getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
