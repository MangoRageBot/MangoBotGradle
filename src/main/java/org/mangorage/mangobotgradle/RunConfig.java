package org.mangorage.mangobotgradle;

import org.mangorage.mangobotgradle.core.Constants;

import java.util.ArrayList;
import java.util.List;

public class RunConfig {
    private final List<String> arguments = new ArrayList<>();
    private String group = Constants.BOT_TASKS_GROUP;
    private String name = "runBot";

    public RunConfig() {}

    public void setName(String name) {
        this.name = name;
    }

    public void addArgs(String... args) {
        arguments.addAll(
                List.of(args)
        );
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return List.copyOf(arguments);
    }
}
