/*
 * Copyright 2010 the original author or authors.
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

import org.gradle.api.GradleException;
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.tasks.TaskExecutor;
import org.gradle.api.internal.tasks.TaskExecutorResult;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskExecutionOutcome;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.api.specs.Spec;
import org.gradle.internal.Cast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link org.gradle.api.internal.tasks.TaskExecutor} which skips tasks whose onlyIf predicate evaluates to false
 */
public class SkipOnlyIfTaskExecutor implements TaskExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipOnlyIfTaskExecutor.class);
    private final TaskExecutor executor;

    public SkipOnlyIfTaskExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public TaskExecutorResult execute(TaskInternal task, TaskStateInternal state, TaskExecutionContext context) {
        Spec<? super TaskInternal> unsatisfiedSpec = null;
        try {
            Spec<? super TaskInternal> onlyIf = task.getOnlyIf();
            // Some third-party plugins override getOnlyIf, returning a generic Spec
            if (onlyIf instanceof DescribingAndSpec) {
                DescribingAndSpec<? super TaskInternal> describingAndSpec = Cast.uncheckedCast(onlyIf);
                unsatisfiedSpec = describingAndSpec.findUnsatisfiedSpec(task);
            } else {
                if (!onlyIf.isSatisfiedBy(task)) {
                    unsatisfiedSpec = onlyIf;
                }
            }
        } catch (Throwable t) {
            state.setOutcome(new GradleException(String.format("Could not evaluate onlyIf predicate for %s.", task), t));
            return TaskExecutorResult.WITHOUT_OUTPUTS;
        }

        if (unsatisfiedSpec != null) {
            if (unsatisfiedSpec instanceof SelfDescribingSpec) {
                SelfDescribingSpec<? super TaskInternal> selfDescribingSpec = Cast.uncheckedCast(unsatisfiedSpec);
                LOGGER.info("Skipping {} as task onlyIf '{}' is false.", task, selfDescribingSpec.getDisplayName());
                state.setSkipReasonMessage("'" + selfDescribingSpec.getDisplayName() + "' not satisfied");
            } else {
                LOGGER.info("Skipping {} as task onlyIf is false.", task);
                state.setSkipReasonMessage("onlyIf not satisfied");
            }
            state.setOutcome(TaskExecutionOutcome.SKIPPED);
            return TaskExecutorResult.WITHOUT_OUTPUTS;
        }

        return executor.execute(task, state, context);
    }
}
