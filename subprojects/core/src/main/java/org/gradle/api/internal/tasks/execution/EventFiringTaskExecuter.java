/*
 * Copyright 2018 the original author or authors.
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
import org.gradle.api.internal.tasks.TaskExecutor;
import org.gradle.api.internal.tasks.TaskExecutorResult;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.execution.taskgraph.TaskListenerInternal;
import org.gradle.internal.logging.slf4j.ContextAwareTaskLogger;
import org.gradle.internal.operations.BuildOperationCategory;
import org.gradle.internal.operations.BuildOperationContext;
import org.gradle.internal.operations.BuildOperationDescriptor;
import org.gradle.internal.operations.BuildOperationRef;
import org.gradle.internal.operations.BuildOperationRunner;
import org.gradle.internal.operations.CallableBuildOperation;

@SuppressWarnings("deprecation")
public class EventFiringTaskExecutor implements TaskExecutor {

    private final BuildOperationRunner buildOperationRunner;
    private final org.gradle.api.execution.TaskExecutionListener taskExecutionListener;
    private final TaskListenerInternal taskListener;
    private final TaskExecutor delegate;

    public EventFiringTaskExecutor(BuildOperationRunner buildOperationRunner, org.gradle.api.execution.TaskExecutionListener taskExecutionListener, TaskListenerInternal taskListener, TaskExecutor delegate) {
        this.buildOperationRunner = buildOperationRunner;
        this.taskExecutionListener = taskExecutionListener;
        this.taskListener = taskListener;
        this.delegate = delegate;
    }

    @Override
    public TaskExecutorResult execute(final TaskInternal task, final TaskStateInternal state, final TaskExecutionContext context) {
        return buildOperationRunner.call(new CallableBuildOperation<TaskExecutorResult>() {
            @Override
            public TaskExecutorResult call(BuildOperationContext operationContext) {
                TaskExecutorResult result = executeTask(operationContext);
                operationContext.setStatus(state.getFailure() != null ? "FAILED" : state.getSkipMessage());
                operationContext.failed(state.getFailure());
                return result;
            }

            private TaskExecutorResult executeTask(BuildOperationContext operationContext) {
                Logger logger = task.getLogger();
                ContextAwareTaskLogger contextAwareTaskLogger = null;
                try {
                    taskListener.beforeExecute(task.getTaskIdentity());
                    taskExecutionListener.beforeExecute(task);
                    if (logger instanceof ContextAwareTaskLogger) {
                        contextAwareTaskLogger = (ContextAwareTaskLogger) logger;
                        BuildOperationRef currentOperation = buildOperationRunner.getCurrentOperation();
                        contextAwareTaskLogger.setFallbackBuildOperationId(currentOperation.getId());
                    }
                } catch (Throwable t) {
                    state.setOutcome(new TaskExecutionException(task, t));
                    return TaskExecutorResult.WITHOUT_OUTPUTS;
                }

                TaskExecutorResult result = delegate.execute(task, state, context);

                if (contextAwareTaskLogger != null) {
                    contextAwareTaskLogger.setFallbackBuildOperationId(null);
                }
                operationContext.setResult(new ExecuteTaskBuildOperationResult(
                    state,
                    result.getCachingState(),
                    result.getReusedOutputOriginMetadata().orElse(null),
                    result.executedIncrementally(),
                    result.getExecutionReasons()
                ));

                try {
                    taskExecutionListener.afterExecute(task, state);
                    taskListener.afterExecute(task.getTaskIdentity(), state);
                } catch (Throwable t) {
                    state.addFailure(new TaskExecutionException(task, t));
                }

                return result;
            }

            @Override
            public BuildOperationDescriptor.Builder description() {
                ExecuteTaskBuildOperationDetails taskOperation = new ExecuteTaskBuildOperationDetails(context.getLocalTaskNode());
                return BuildOperationDescriptor.displayName("Task " + task.getIdentityPath())
                    .name(task.getIdentityPath().toString())
                    .progressDisplayName(task.getIdentityPath().toString())
                    .metadata(BuildOperationCategory.TASK)
                    .details(taskOperation);
            }
        });
    }
}
