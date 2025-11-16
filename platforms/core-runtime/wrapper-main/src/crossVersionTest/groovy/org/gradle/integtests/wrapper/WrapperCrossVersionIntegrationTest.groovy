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
package org.gradle.integtests.wrapper

import org.gradle.integtests.fixtures.daemon.DaemonLogsAnalyzer
import org.gradle.integtests.fixtures.executor.GradleDistribution
import org.gradle.integtests.fixtures.executor.GradleExecutor
import org.gradle.test.precondition.Requires
import org.gradle.test.preconditions.UnitTestPreconditions

@SuppressWarnings("IntegrationTestFixtures")
class WrapperCrossVersionIntegrationTest extends AbstractWrapperCrossVersionIntegrationTest {
    @Requires(value = [
        UnitTestPreconditions.NotWindowsJavaBefore11
    ], reason = "see https://github.com/gradle/gradle-private/issues/3758")
    void canUseWrapperFromPreviousVersionToRunCurrentVersion() {
        when:
        GradleExecutor executor = prepareWrapperExecutor(previous, current)

        then:
        checkWrapperWorksWith(executor, current)

        cleanup:
        cleanupDaemons(executor, current)
    }

    @Requires(value = [UnitTestPreconditions.NotWindowsJavaBefore11], reason = "https://github.com/gradle/gradle-private/issues/3758")
    void canUseWrapperFromCurrentVersionToRunPreviousVersion() {
        when:
        GradleExecutor executor = prepareWrapperExecutor(current, previous).withWarningMode(null)

        then:
        checkWrapperWorksWith(executor, previous)

        cleanup:
        cleanupDaemons(executor, previous)
    }

    void checkWrapperWorksWith(GradleExecutor executor, GradleDistribution executionVersion) {
        def result = executor.withTasks('hello').run()

        assert result.output.contains("hello from $executionVersion.version.version")
        assert result.output.contains("using distribution at ${executor.gradleUserHomeDir.file("wrapper/dists")}")
        assert result.output.contains("using Gradle user home at $executor.gradleUserHomeDir")
    }

    static void cleanupDaemons(GradleExecutor executor, GradleDistribution executionVersion) {
        new DaemonLogsAnalyzer(executor.daemonBaseDir, executionVersion.version.version).killAll()
    }
}
