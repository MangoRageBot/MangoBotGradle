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

package org.mangorage.mangobotgradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mangorage.mangobotgradle.core.Constants;
import org.mangorage.mangobotgradle.core.TaskRegistry;
import org.mangorage.mangobotgradle.tasks.DatagenTask;
import org.mangorage.mangobotgradle.tasks.RunBotTask;
import org.mangorage.mangobotgradle.tasks.RunInstallerTask;
import org.mangorage.mangobotgradle.tasks.SetupPluginsTask;

import java.util.Objects;

public final class MangoBotGradlePlugin implements Plugin<Project> {


    public static Config cfg = null;

    private final Config config = new Config();
    private final TaskRegistry taskRegistry = new TaskRegistry();

    public TaskRegistry getTaskRegistry() {
        return taskRegistry;
    }

    public Config getConfig() {
        return config;
    }

    public MangoBotGradlePlugin() {
        taskRegistry.register(t -> {
            MangoBotGradlePlugin.cfg = config;

            t.register("setupPlugins", SetupPluginsTask.class, config, Constants.BOT_OTHER_TASKS_GROUP);

            t.register("runInstaller", RunInstallerTask.class, Constants.INSTALLER_TASKS_GROUP);

            config.getRunConfigs().forEach(cfg -> {
                t.register(
                        cfg.getName(),
                        RunBotTask.class,
                        cfg
                );
            });
        });
    }

    @Override
    public void apply(Project project) {
        project.getExtensions().add("MangoBotConfig", config);

        project.getConfigurations().create("installer", t -> {
            t.setVisible(true);
        });

        var botCfg = project.getConfigurations().create("bootstrap", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        var plugin = project.getConfigurations().create("plugin", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        var library = project.getConfigurations().create("library", t -> {
            t.setVisible(true);
            t.setTransitive(true);
            t.setCanBeResolved(true);
        });

        var embeddedLibrary = project.getConfigurations().create("embedLibrary", t -> {
            t.setVisible(true);
        });

        project.getConfigurations().findByName("implementation").extendsFrom(botCfg, plugin, library, embeddedLibrary);

        project.afterEvaluate(a -> {
            Objects.requireNonNull(config.getJarTask(), "jarTask cannot be null!");
            taskRegistry.apply(project);
            DatagenTask.apply(project, this);
        });
    }
}
