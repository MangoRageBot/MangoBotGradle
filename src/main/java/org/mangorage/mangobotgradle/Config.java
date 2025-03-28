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

import org.gradle.api.Task;
import org.gradle.jvm.tasks.Jar;
import org.mangorage.mangobotgradle.core.resolvers.Resolver;
import org.mangorage.mangobotgradle.tasks.DatagenTask;

import java.util.function.Supplier;

public final class Config {
    private boolean pluginDevMode = true;
    private Jar jarTask;
    private Supplier<Task> releaseTask = () -> null;

    public Config() {}

    public void setJarTask(Jar jar) {
        this.jarTask = jar;
    }

    public Jar getJarTask() {
        return jarTask;
    }

    public void disableCopyOverBot() {
        this.pluginDevMode = false;
    }

    public boolean isPluginDevMode() {
        return this.pluginDevMode;
    }

    public void addResolver(Resolver resolver) {
        DatagenTask.add(resolver);
    }
}
