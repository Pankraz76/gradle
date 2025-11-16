/*
 * Copyright 2025 the original author or authors.
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

package org.gradle.docs.samples;

import org.gradle.api.logging.configuration.WarningMode;
import org.gradle.exemplar.executor.CommandExecutor;
import org.gradle.integtests.fixtures.AvailableJavaHomes;
import org.gradle.integtests.fixtures.executor.ExecutionFailure;
import org.gradle.integtests.fixtures.executor.ExecutionResult;
import org.gradle.integtests.fixtures.executor.GradleContextualExecutor;
import org.gradle.integtests.fixtures.executor.GradleDistribution;
import org.gradle.integtests.fixtures.executor.GradleExecutor;
import org.gradle.integtests.fixtures.executor.IntegrationTestBuildContext;
import org.gradle.integtests.fixtures.executor.UnderDevelopmentGradleDistribution;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.jvm.Jvm;
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

class IntegrationTestSamplesExecutor extends CommandExecutor {

    private static final String WARNING_MODE_FLAG_PREFIX = "--warning-mode=";

    private static final String NO_STACKTRACE_CHECK = "-Dorg.gradle.sampletest.noStackTraceCheck=true";

    private static final String SAMPLE_ENV_PREFIX = "-Dorg.gradle.sampletest.env.";

    private final File workingDir;
    private final boolean expectFailure;
    private final GradleExecutor gradle;

    IntegrationTestSamplesExecutor(File workingDir, boolean expectFailure) {
        this.workingDir = workingDir;
        this.expectFailure = expectFailure;
        GradleDistribution distribution = new UnderDevelopmentGradleDistribution(IntegrationTestBuildContext.INSTANCE);
        this.gradle = new GradleContextualExecutor(distribution, new TestNameTestDirectoryProvider(IntegrationTestSamplesExecutor.class), IntegrationTestBuildContext.INSTANCE);
    }

    @Override
    protected int run(String executable, List<String> args, List<String> flags, OutputStream outputStream) {
        try {
            GradleExecutor executor = createExecutor(args, flags);
            if (expectFailure) {
                // TODO(mlopatkin) sometimes it is still possible to save the configuration cache state in the event of failure.
                //  We need to figure out how to separate expected failure from the CC store failure.
                ExecutionFailure result = executor.runWithFailure();
                outputStream.write((result.getOutput() + result.getError()).getBytes());
            } else {
                ExecutionResult result = executor.run();
                outputStream.write(result.getOutput().getBytes());
            }
            return expectFailure ? 1 : 0;
        } catch (IOException e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }

    private GradleExecutor createExecutor(List<String> args, List<String> flags) {
        WarningMode warningMode = flags.stream()
            .filter(it -> it.startsWith(WARNING_MODE_FLAG_PREFIX))
            .map(it -> WarningMode.valueOf(capitalize(it.replace(WARNING_MODE_FLAG_PREFIX, "").toLowerCase())))
            .findFirst().orElse(WarningMode.Fail);
        List<String> filteredFlags = flags.stream()
            .filter(it -> !it.startsWith(WARNING_MODE_FLAG_PREFIX) && !it.equals(NO_STACKTRACE_CHECK) && !it.startsWith(SAMPLE_ENV_PREFIX))
            .collect(toCollection(ArrayList::new));
        filteredFlags.add(getAvailableJdksFlag());
        GradleExecutor executor = gradle.inDirectory(workingDir).ignoreMissingSettingsFile()
            .noDeprecationChecks()
            .withWarningMode(warningMode)
            .withToolchainDetectionEnabled()
            .withArguments(filteredFlags)
            .withArgument("--no-problems-report")
            .withTasks(args);

        if (flags.contains("--build-cache")) {
            // > Failed to load cache entry b982a8cf9ce337cea7c2eacd8bb478fb for task ':bundle': Could not load from local cache:
            //   Timeout waiting to lock Build cache (/mnt/tcagent1/work/b6cfc23ab10332e6/intTestHomeDir/distributions-full/caches/build-cache-1).
            //   It is currently in use by another process.
            //    Owner PID: 5250
            //    Our PID: 4769
            //    Owner Operation:
            //    Our operation:
            //    Lock file: /mnt/tcagent1/work/b6cfc23ab10332e6/intTestHomeDir/distributions-full/caches/build-cache-1/build-cache-1.lock
            executor.withGradleUserHomeDir(new File(workingDir, "user-home"));
        }

        if (flags.stream().anyMatch(NO_STACKTRACE_CHECK::equals)) {
            executor.withStackTraceChecksDisabled();
        }

        Map<String, String> env = flags.stream()
            .filter(it -> it.startsWith(SAMPLE_ENV_PREFIX))
            .map(it -> it.replace(SAMPLE_ENV_PREFIX, "").split("="))
            .filter(it -> it.length == 2)
            .collect(toMap(it -> it[0], it -> it[1]));
        if (!env.isEmpty()) {
            executor.withEnvironmentVars(env);
        }
        return executor;
    }

    private String getAvailableJdksFlag() {
        String allJdkPaths = AvailableJavaHomes.getAvailableJvms().stream()
            .map(Jvm::getJavaHome)
            .map(File::getAbsolutePath)
            .collect(Collectors.joining(","));
        return "-Dorg.gradle.java.installations.paths=" + allJdkPaths;
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
