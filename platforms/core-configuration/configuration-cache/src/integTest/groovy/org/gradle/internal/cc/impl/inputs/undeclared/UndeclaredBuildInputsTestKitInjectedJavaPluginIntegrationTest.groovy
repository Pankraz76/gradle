/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.cc.impl.inputs.undeclared

import org.gradle.integtests.fixtures.daemon.DaemonLogsAnalyzer
import org.gradle.integtests.fixtures.executor.AbstractGradleExecutor
import org.gradle.integtests.fixtures.executor.ExecutionFailure
import org.gradle.integtests.fixtures.executor.ExecutionResult
import org.gradle.integtests.fixtures.executor.GradleDistribution
import org.gradle.integtests.fixtures.executor.GradleExecutor
import org.gradle.integtests.fixtures.executor.IntegrationTestBuildContext
import org.gradle.integtests.fixtures.executor.OutputScrapingExecutionFailure
import org.gradle.integtests.fixtures.executor.OutputScrapingExecutionResult
import org.gradle.internal.nativeintegration.services.NativeServices
import org.gradle.test.fixtures.file.TestDirectoryProvider
import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.precondition.Requires
import org.gradle.test.preconditions.UnitTestPreconditions
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.ToolingApiGradleExecutor
import org.gradle.util.SetSystemProperties
import org.junit.Rule

@Requires(UnitTestPreconditions.NotWindows)
class UndeclaredBuildInputsTestKitInjectedJavaPluginIntegrationTest extends AbstractUndeclaredBuildInputsIntegrationTest implements JavaPluginImplementation {
    TestFile jar
    TestFile testKitDir

    @Override
    String getLocation() {
        return "Plugin 'sneaky'"
    }

    @Rule
    SetSystemProperties setSystemProperties = new SetSystemProperties(
        (NativeServices.NATIVE_DIR_OVERRIDE): buildContext.nativeServicesDir.absolutePath
    )

    @Override
    GradleExecutor createExecutor() {
        testKitDir = file("test-kit")
        def executor = new TestKitBackedGradleExecutor(distribution, temporaryFolder, getBuildContext(), testKitDir)
        jar = file("plugins/sneaky.jar")
        executor.pluginClasspath.add(jar)
        return executor
    }

    def cleanup() {
        DaemonLogsAnalyzer.newAnalyzer(testKitDir.file(ToolingApiGradleExecutor.TEST_KIT_DAEMON_DIR_NAME)).killAll()
    }

    @Override
    void buildLogicApplication(BuildInputRead read) {
        def builder = artifactBuilder()
        javaPlugin(builder.sourceFile("SneakyPlugin.java"), read)
        builder.resourceFile("META-INF/gradle-plugins/sneaky.properties") << """
implementation-class: SneakyPlugin
        """
        builder.buildJar(jar)
        buildFile << """
            plugins { id("sneaky") }
        """
    }

    static class TestKitBackedGradleExecutor extends AbstractGradleExecutor {
        List<File> pluginClasspath = []
        private final IntegrationTestBuildContext buildContext
        private final TestFile testKitDir

        TestKitBackedGradleExecutor(GradleDistribution distribution, TestDirectoryProvider testDirectoryProvider, IntegrationTestBuildContext buildContext, TestFile testKitDir) {
            super(distribution, testDirectoryProvider)
            this.testKitDir = testKitDir
            this.buildContext = buildContext
        }

        @Override
        void assertCanExecute() throws AssertionError {
        }

        @Override
        protected ExecutionResult doRun() {
            def runnerResult = createRunner().build()
            return OutputScrapingExecutionResult.from(runnerResult.output, "")
        }

        @Override
        protected ExecutionFailure doRunWithFailure() {
            def runnerResult = createRunner().buildAndFail()
            return OutputScrapingExecutionFailure.from(runnerResult.output, "")
        }

        private GradleRunner createRunner() {
            def runner = GradleRunner.create()
            if (!IntegrationTestBuildContext.embedded) {
                runner.withGradleInstallation(buildContext.gradleHomeDir)
            }
            runner.withTestKitDir(testKitDir)
            runner.withProjectDir(workingDir)
            def args = allArgs
            args.remove("--no-daemon")
            runner.withArguments(args)
            runner.withPluginClasspath(pluginClasspath)
            runner.withEnvironment(environmentVars)
            runner.forwardOutput()
            runner
        }
    }
}
