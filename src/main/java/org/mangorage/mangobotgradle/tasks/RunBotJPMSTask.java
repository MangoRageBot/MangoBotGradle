package org.mangorage.mangobotgradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.JavaExec;
import org.mangorage.mangobotgradle.Config;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class RunBotJPMSTask extends JavaExec {
    @Inject
    public RunBotJPMSTask(Config config, String group, List<String> args) {
        setGroup(group);
        setDescription("Runs the bot");

        ArrayList<Task> deps = new ArrayList<>();
        deps.addAll(getProject().getTasksByName("copyTask", false));
        deps.addAll(getProject().getTasksByName("runInstaller", false));

        setDependsOn(deps);
        mustRunAfter(deps);

        setWorkingDir(getProject().file("build/run/"));
        setArgs(args);
        getMainClass().set("");

        // Create your module path from the config
        FileCollection modulePath = getProject().getConfigurations().getByName(
                config.isPluginDevMode() ? "bot" : "botInternal"
        );

        setClasspath(modulePath); // EMPTY CLASSPATH, this is MODULE mode
        getMainClass().set("org.mangorage.bootstrap.Bootstrap");
        getMainModule().set("org.mangorage.bootstrap");
        getModularity().getInferModulePath().set(true);
    }
}
