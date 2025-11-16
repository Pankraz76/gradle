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
package org.gradle.integtests.fixtures

import org.gradle.integtests.fixtures.compatibility.CrossVersionTest
import org.gradle.integtests.fixtures.executor.GradleDistribution
import org.gradle.integtests.fixtures.executor.GradleExecutor
import org.gradle.integtests.fixtures.executor.IntegrationTestBuildContext
import org.gradle.integtests.fixtures.executor.NoDaemonGradleExecutor
import org.gradle.integtests.fixtures.executor.UnderDevelopmentGradleDistribution
import org.gradle.test.fixtures.file.CleanupTestDirectory
import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.gradle.test.fixtures.maven.MavenFileRepository
import org.junit.Rule
import spock.lang.Retry
import spock.lang.Specification

import static spock.lang.Retry.Mode.SETUP_FEATURE_CLEANUP

/**
 * For running these tests against specific versions, see {@link org.gradle.integtests.fixtures.compatibility.AbstractContextualMultiVersionTestInterceptor}
 */
@CrossVersionTest
@Retry(condition = { RetryConditions.onIssueWithReleasedGradleVersion(instance, failure) }, mode = SETUP_FEATURE_CLEANUP, count = 2)
@CleanupTestDirectory
abstract class CrossVersionIntegrationSpec extends Specification {
    @Rule TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider(getClass())
    private final List<GradleExecutor> executors = []
    final GradleDistribution current = new UnderDevelopmentGradleDistribution()
    static GradleDistribution previous
    private MavenFileRepository mavenRepo
    private TestFile gradleUserHomeDir

    boolean retryWithCleanProjectDir() {
        temporaryFolder.testDirectory.listFiles().each {
            it.deleteDir()
        }
        true
    }

    def cleanup() {
        executors.each { it.cleanup() }
    }

    void requireOwnGradleUserHomeDir() {
        gradleUserHomeDir = file("user-home")
    }

    GradleDistribution getPrevious() {
        return previous
    }

    String getReleasedGradleVersion() {
        return previous.version.baseVersion.version
    }

    protected TestFile getBuildFile() {
        testDirectory.file('build.gradle')
    }

    protected TestFile getSettingsFile() {
        testDirectory.file('settings.gradle')
    }

    TestFile getTestDirectory() {
        temporaryFolder.getTestDirectory()
    }

    protected TestFile file(Object... path) {
        testDirectory.file(path)
    }

    protected MavenFileRepository getMavenRepo() {
        if (mavenRepo == null) {
            mavenRepo = new MavenFileRepository(file("maven-repo"))
        }
        return mavenRepo
    }

    GradleExecutor version(GradleDistribution dist) {
        def executor = new NoDaemonGradleExecutor(dist, temporaryFolder, IntegrationTestBuildContext.INSTANCE)
        if (gradleUserHomeDir) {
            executor.withGradleUserHomeDir(gradleUserHomeDir)
        }
        executor.inDirectory(testDirectory)
        executors << executor
        return executor
    }
}
