package org.mangorage.mangobotgradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.JavaExec;
import org.mangorage.mangobotgradle.RunConfig;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class RunInstallerAndBootTask extends JavaExec {
    @Inject
    public RunInstallerAndBootTask(RunConfig runConfig) {
        setGroup(runConfig.getGroup());
        setDescription("Runs the bot");

        ArrayList<Task> deps = new ArrayList<>();
        deps.addAll(getProject().getTasksByName("copyTask", false));
        deps.addAll(getProject().getTasksByName("setupPlugins", false));

        setDependsOn(deps);
        mustRunAfter(deps);

        setWorkingDir(getProject().file("build/run/"));

        Path pluginsPath = getProject()
                .getProjectDir()
                .toPath()
                .resolve("build/run/plugins");

        List<String> pluginJars = new ArrayList<>();

        File pluginsDir = pluginsPath.toFile();
        if (pluginsDir.exists() && pluginsDir.isDirectory()) {
            File[] files = pluginsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        pluginJars.add(file.getAbsolutePath());
                    }
                }
            }
        }

        List<String> argsForProgram = new ArrayList<>(runConfig.getArgs());
        argsForProgram.add("-launch");

        if (!pluginJars.isEmpty()) {
            String jarsArg = String.join(";", pluginJars);
            argsForProgram.add("-manualJar");
            argsForProgram.add(jarsArg);
        }

        setArgs(argsForProgram);

        FileCollection modulePath =
                getProject().getConfigurations().getByName("installer");

        setClasspath(modulePath); // EMPTY CLASSPATH, this is MODULE mode
    }
}
