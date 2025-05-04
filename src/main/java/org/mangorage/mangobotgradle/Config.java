/*
 * Copyright (c) 2023-2024. MangoRage
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

import org.gradle.api.Action;
import org.gradle.jvm.tasks.Jar;
import org.mangorage.mangobotgradle.core.resolvers.Resolver;
import org.mangorage.mangobotgradle.tasks.DatagenTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Config {
    private final List<RunConfig> runConfigs = new ArrayList<>();
    private final List<RunConfig> runConfigs_readOnly = Collections.unmodifiableList(runConfigs);
    private final RunConfig defaultRunConfig = new RunConfig();
    private Jar jarTask;

    public Config() {}

    public void setJarTask(Jar jar) {
        this.jarTask = jar;
    }

    public Jar getJarTask() {
        return jarTask;
    }

    public void addResolver(Resolver resolver) {
        DatagenTask.add(resolver);
    }

    public void addRunConfig(Action<RunConfig> action) {
        RunConfig runConfig = new RunConfig(); // or however you're instantiating this garbage
        action.execute(runConfig);
        runConfigs.add(runConfig);
    }

    public void useDefaultRunConfig() {
        runConfigs.add(defaultRunConfig);
    }

    public List<RunConfig> getRunConfigs() {
        return runConfigs_readOnly;
    }
}