package org.mangorage.mangobotgradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.JavaExec;
import org.mangorage.mangobotgradle.RunConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class RunInstallerAndBootTask extends JavaExec {
    @Inject
    public RunInstallerAndBootTask(RunConfig runConfig) {
        setGroup(runConfig.getGroup());
        setDescription("Runs the bot");

        ArrayList<Task> deps = new ArrayList<>(getProject().getTasksByName("copyTask", false));

        setDependsOn(deps);
        mustRunAfter(deps);

        setWorkingDir(getProject().file("build/run/"));
        setArgs(runConfig.getArgs());

        // Create your module path from the config
        FileCollection modulePath = getProject().getConfigurations().getByName("installer");


        setClasspath(modulePath); // EMPTY CLASSPATH, this is MODULE mode
        getMainClass().set("org.mangorage.installer.Installer");
        getMainModule().set("org.mangorage.installer");
        getModularity().getInferModulePath().set(true);

        setJvmArgs(List.of("--add-modules", "java.scripting", "--add-modules", "java.instrument", "--add-modules", "java.sql", "--add-modules", "jdk.unsupported"));
    }
}
