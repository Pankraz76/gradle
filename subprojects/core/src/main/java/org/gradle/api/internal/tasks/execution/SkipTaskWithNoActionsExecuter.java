/*
 * Copyright 2011 the original author or authors.
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

import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.tasks.TaskExecutor;
import org.gradle.api.internal.tasks.TaskExecutorResult;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskExecutionOutcome;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link org.gradle.api.internal.tasks.TaskExecutor} which skips tasks that have no actions.
 */
public class SkipTaskWithNoActionsExecutor implements TaskExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipTaskWithNoActionsExecutor.class);
    private final TaskExecutionGraph taskExecutionGraph;
    private final TaskExecutor executor;

    public SkipTaskWithNoActionsExecutor(TaskExecutionGraph taskExecutionGraph, TaskExecutor executor) {
        this.taskExecutionGraph = taskExecutionGraph;
        this.executor = executor;
    }

    @Override
    public TaskExecutorResult execute(TaskInternal task, TaskStateInternal state, TaskExecutionContext context) {
        if (!task.hasTaskActions()) {
            LOGGER.info("Skipping {} as it has no actions.", task);
            boolean upToDate = true;
            for (Task dependency : taskExecutionGraph.getDependencies(task)) {
                if (!dependency.getState().getSkipped()) {
                    upToDate = false;
                    break;
                }
            }
            state.setActionable(false);
            state.setOutcome(upToDate ? TaskExecutionOutcome.UP_TO_DATE : TaskExecutionOutcome.EXECUTED);
            return TaskExecutorResult.WITHOUT_OUTPUTS;
        }
        return executor.execute(task, state, context);
    }
}
