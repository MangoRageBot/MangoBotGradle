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

        Path plugins = getProject().getProjectDir().toPath().resolve("build/run/plugins");
        StringBuilder builder = new StringBuilder();

        for (File file : plugins.toFile().listFiles()) {
            System.out.println(file.getName());
            if (!file.isDirectory() && file.getName().contains(".jar"))
                builder.append(file.toPath().toAbsolutePath()).append(";");
        }

        String args = builder.substring(0, builder.length() - 1);
        var finalArgs = List.of("-manualJar", args);

        List<String> argsForProgram = new ArrayList<>();
        argsForProgram.addAll(runConfig.getArgs());
        argsForProgram.add("-launch");
        argsForProgram.add("-manualJar");
        argsForProgram.add(args);
        setArgs(argsForProgram);

        // Create your module path from the config
        FileCollection modulePath = getProject().getConfigurations().getByName("installer");


        setClasspath(modulePath); // EMPTY CLASSPATH, this is MODULE mode
        getMainClass().set("org.mangorage.installer.Installer");
        getMainModule().set("org.mangorage.installer");
        getModularity().getInferModulePath().set(true);

        setJvmArgs(List.of("--add-modules", "java.scripting", "--add-modules", "java.instrument", "--add-modules", "java.sql", "--add-modules", "jdk.unsupported"));
    }
}
