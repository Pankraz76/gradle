/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.execution;

import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.changedetection.TaskExecutionMode;
import org.gradle.api.internal.changedetection.TaskExecutionModeResolver;
import org.gradle.api.internal.tasks.TaskExecutor;
import org.gradle.api.internal.tasks.TaskExecutorResult;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.internal.time.Time;
import org.gradle.internal.time.Timer;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public class ResolveTaskExecutionModeExecutor implements TaskExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolveTaskExecutionModeExecutor.class);

    private final TaskExecutor executor;
    private final TaskExecutionModeResolver executionModeResolver;

    public ResolveTaskExecutionModeExecutor(TaskExecutionModeResolver executionModeResolver, TaskExecutor executor) {
        this.executor = executor;
        this.executionModeResolver = executionModeResolver;
    }

    @Override
    public TaskExecutorResult execute(final TaskInternal task, TaskStateInternal state, final TaskExecutionContext context) {
        Timer clock = Time.startTimer();
        TaskExecutionMode taskExecutionMode = executionModeResolver.getExecutionMode(task, context.getTaskProperties());
        context.setTaskExecutionMode(taskExecutionMode);
        LOGGER.debug("Putting task artifact state for {} into context took {}.", task, clock.getElapsed());
        try {
            return executor.execute(task, state, context);
        } finally {
            context.setTaskExecutionMode(null);
            LOGGER.debug("Removed task artifact state for {} from context.", task);
        }
    }

}
