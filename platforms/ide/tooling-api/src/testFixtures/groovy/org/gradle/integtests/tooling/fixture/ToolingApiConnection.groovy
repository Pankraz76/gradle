/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.integtests.tooling.fixture

import org.apache.commons.io.output.TeeOutputStream
import org.gradle.tooling.BuildAction
import org.gradle.tooling.BuildActionExecutor
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.ConfigurableLauncher
import org.gradle.tooling.IntermediateResultHandler
import org.gradle.tooling.ModelBuilder
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.TestLauncher

import static org.gradle.util.DebugUtil.DAEMON_DEBUG_PORT
import static org.gradle.util.DebugUtil.isDebuggerAttached

trait ToolingApiConfigurableLauncher<T extends ConfigurableLauncher<T>> {
    T configurableLauncher
    OutputStream stdout
    OutputStream stderr

    def initTrait(T configurableLauncher, OutputStream stdout, OutputStream stderr) {
        this.configurableLauncher = configurableLauncher
        configurableLauncher.standardOutput = stdout
        configurableLauncher.standardError = stderr
        this.stdout = stdout
        this.stderr = stderr
        if(isDebuggerAttached()){
            configurableLauncher.setJvmArguments("-agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=${DAEMON_DEBUG_PORT}")
        }
    }

    T setStandardOutput(OutputStream outputStream) {
        configurableLauncher.standardOutput = new TeeOutputStream(outputStream, stdout)
        return configurableLauncher
    }

    T setStandardError(OutputStream outputStream) {
        configurableLauncher.standardError = new TeeOutputStream(outputStream, stderr)
        return configurableLauncher
    }
}

class ToolingApiBuildLauncher implements BuildLauncher, ToolingApiConfigurableLauncher<BuildLauncher> {
    @Delegate
    private final BuildLauncher buildLauncher

    ToolingApiBuildLauncher(BuildLauncher buildLauncher, OutputStream stdout, OutputStream stderr) {
        initTrait(buildLauncher, stdout, stderr)
        this.buildLauncher = buildLauncher
    }
}

class ToolingApiTestLauncher implements TestLauncher, ToolingApiConfigurableLauncher<TestLauncher> {
    @Delegate
    private final TestLauncher testLauncher

    ToolingApiTestLauncher(TestLauncher testLauncher, OutputStream stdout, OutputStream stderr) {
        initTrait(testLauncher, stdout, stderr)
        this.testLauncher = testLauncher
    }
}

class ToolingApiBuildActionExecutor<T> implements BuildActionExecutor<T>, ToolingApiConfigurableLauncher<BuildActionExecutor<T>> {
    @Delegate
    private final BuildActionExecutor<T> buildActionExecutor

    ToolingApiBuildActionExecutor(BuildActionExecutor<T> buildActionExecutor, OutputStream stdout, OutputStream stderr) {
        initTrait(buildActionExecutor, stdout, stderr)
        this.buildActionExecutor = buildActionExecutor
    }
}

class ToolingApiModelBuilder<T> implements ModelBuilder<T>, ToolingApiConfigurableLauncher<ModelBuilder<T>> {
    @Delegate
    private final ModelBuilder<T> modelBuilder

    ToolingApiModelBuilder(ModelBuilder<T> modelBuilder, OutputStream stdout, OutputStream stderr) {
        initTrait(modelBuilder, stdout, stderr)
        this.modelBuilder = modelBuilder
    }
}

class BuildActionExecutorBuilder implements BuildActionExecutor.Builder {

    private final BuildActionExecutor.Builder delegate
    private final OutputStream stderr
    private final OutputStream stdout

    BuildActionExecutorBuilder(BuildActionExecutor.Builder delegate, OutputStream stdout, OutputStream stderr) {
        this.delegate = delegate
        this.stdout = stdout
        this.stderr = stderr
    }

    @Override
    <T> BuildActionExecutor.Builder projectsLoaded(BuildAction<T> buildAction, IntermediateResultHandler<? super T> handler) throws IllegalArgumentException {
        delegate.projectsLoaded(buildAction, handler)
        this
    }

    @Override
    <T> BuildActionExecutor.Builder buildFinished(BuildAction<T> buildAction, IntermediateResultHandler<? super T> handler) throws IllegalArgumentException {
        delegate.buildFinished(buildAction, handler)
        this
    }

    @Override
    BuildActionExecutor<Void> build() {
        new ToolingApiBuildActionExecutor(delegate.build(), stdout, stderr)
    }
}

/**
 * This trait is used to add the missing methods to the ToolingApiConnection class without actually deriving from ProjectConnection.
 * While still allowing the ToolingApiConnection to be used as a ProjectConnection.
 * This avoids loading the ProjectConnection class from the test code and postpones loading to the tooling api magic.
 */
trait ProjectConnectionTrait implements ProjectConnection {
}

class ToolingApiConnection {
    private final ProjectConnection projectConnection
    private final OutputStream stderr
    private final OutputStream stdout

    ToolingApiConnection(ProjectConnection projectConnection, OutputStream stdout, OutputStream stderr) {
        this.stdout = stdout
        this.stderr = stderr
        this.projectConnection = projectConnection
        this.withTraits(ProjectConnectionTrait)
    }

    def methodMissing(String name, args) {
        projectConnection."$name"(*args)
    }

    BuildActionExecutor.Builder action() {
        new BuildActionExecutorBuilder(projectConnection.action(), stdout, stderr)
    }

    BuildLauncher newBuild() {
        new ToolingApiBuildLauncher(projectConnection.newBuild(), stdout, stderr)
    }

    TestLauncher newTestLauncher() {
        new ToolingApiTestLauncher(projectConnection.newTestLauncher(), stdout, stderr)
    }

    <T> ModelBuilder<T> model(Class<T> modelType) {
        new ToolingApiModelBuilder(projectConnection.model(modelType), stdout, stderr)
    }

    <T> BuildActionExecutor<T> action(BuildAction<T> buildAction) {
        new ToolingApiBuildActionExecutor(projectConnection.action(buildAction), stdout, stderr)
    }
}
