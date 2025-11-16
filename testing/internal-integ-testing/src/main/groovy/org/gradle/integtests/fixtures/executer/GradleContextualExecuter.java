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
package org.gradle.integtests.fixtures.executor;

import org.gradle.integtests.fixtures.timeout.IntegrationTestTimeoutInterceptor;
import org.gradle.test.fixtures.file.TestDirectoryProvider;

import java.nio.charset.Charset;
import java.util.Locale;

import static org.gradle.integtests.fixtures.timeout.IntegrationTestTimeout.DEFAULT_TIMEOUT_SECONDS;

/**
 * Selects a different executor implementation based on the value of a system property.
 *
 * Facilitates running the same test in different execution modes.
 */
public class GradleContextualExecutor extends AbstractDelegatingGradleExecutor {

    private static final String EXECUTER_SYS_PROP = "org.gradle.integtest.executor";

    private Executor executorType;

    private enum Executor {
        embedded(false),
        forking(true),
        noDaemon(true),
        parallel(true, true),
        configCache(true),
        isolatedProjects(true);

        final public boolean forks;
        final public boolean executeParallel;

        Executor(boolean forks) {
            this(forks, false);
        }

        Executor(boolean forks, boolean parallel) {
            this.forks = forks;
            this.executeParallel = parallel;
        }
    }

    private static Executor getSystemPropertyExecutor() {
        return Executor.valueOf(System.getProperty(EXECUTER_SYS_PROP, Executor.forking.toString()));
    }

    public static boolean isNoDaemon() {
        return getSystemPropertyExecutor() == Executor.noDaemon;
    }

    public static boolean isDaemon() {
        return !(isNoDaemon() || IntegrationTestBuildContext.isEmbedded());
    }

    public static boolean isLongLivingProcess() {
        return !isNoDaemon();
    }

    public static boolean isParallel() {
        return getSystemPropertyExecutor().executeParallel;
    }

    public static boolean isNotConfigCache() {
        return !isConfigCache();
    }

    public static boolean isConfigCache() {
        Executor executor = getSystemPropertyExecutor();
        return executor == Executor.configCache || executor == Executor.isolatedProjects;
    }

    public static boolean isNotIsolatedProjects() {
        return !isIsolatedProjects();
    }

    public static boolean isIsolatedProjects() {
        return getSystemPropertyExecutor() == Executor.isolatedProjects;
    }

    private GradleExecutor gradleExecutor;

    public GradleContextualExecutor(GradleDistribution distribution, TestDirectoryProvider testDirectoryProvider, IntegrationTestBuildContext buildContext) {
        super(distribution, testDirectoryProvider, buildContext);
        this.executorType = getSystemPropertyExecutor();
    }

    @Override
    protected GradleExecutor configureExecutor() {
        if (!getClass().desiredAssertionStatus()) {
            throw new RuntimeException("Assertions must be enabled when running integration tests.");
        }

        if (gradleExecutor == null) {
            gradleExecutor = createExecutor(executorType);
        } else {
            gradleExecutor.reset();
        }
        configureExecutor(gradleExecutor);
        try {
            gradleExecutor.assertCanExecute();
        } catch (AssertionError assertionError) {
            if (gradleExecutor instanceof InProcessGradleExecutor) {
                throw new RuntimeException("Running tests with a Gradle distribution in embedded mode is no longer supported.", assertionError);
            }
            gradleExecutor = new NoDaemonGradleExecutor(getDistribution(), getTestDirectoryProvider());
            configureExecutor(gradleExecutor);
        }

        return gradleExecutor;
    }

    private void configureExecutor(GradleExecutor gradleExecutor) {
        copyTo(gradleExecutor);
    }

    private GradleExecutor createExecutor(Executor executorType) {
        switch (executorType) {
            case embedded:
                return new InProcessGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            case noDaemon:
                return new NoDaemonGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            case parallel:
                return new ParallelForkingGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            case forking:
                return new DaemonGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            case configCache:
                return new ConfigurationCacheGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            case isolatedProjects:
                return new IsolatedProjectsGradleExecutor(getDistribution(), getTestDirectoryProvider(), gradleVersion, buildContext);
            default:
                throw new RuntimeException("Not a supported executor type: " + executorType);
        }
    }

    @Override
    public void cleanup() {
        new IntegrationTestTimeoutInterceptor(DEFAULT_TIMEOUT_SECONDS).intercept(ignored -> {
            if (gradleExecutor != null) {
                gradleExecutor.stop();
            }
            GradleContextualExecutor.super.cleanup();
        });

    }

    @Override
    public GradleExecutor ignoreCleanupAssertions() {
        if (gradleExecutor != null) {
            gradleExecutor.ignoreCleanupAssertions();
        }
        return super.ignoreCleanupAssertions();
    }

    @Override
    public GradleExecutor reset() {
        if (gradleExecutor != null) {
            gradleExecutor.reset();
        }
        return super.reset();
    }

    // The following overrides are here instead of in 'InProcessGradleExecutor' due to the way executors are layered+inherited
    // This should be improved as part of https://github.com/gradle/gradle-private/issues/1009

    @Override
    public GradleExecutor withDefaultCharacterEncoding(String defaultCharacterEncoding) {
        if (executorType == Executor.embedded && !Charset.forName(defaultCharacterEncoding).equals(Charset.defaultCharset())) {
            // need to fork to apply the new default character encoding
            requireDaemon().requireIsolatedDaemons();
        }
        return super.withDefaultCharacterEncoding(defaultCharacterEncoding);
    }

    @Override
    public GradleExecutor withDefaultLocale(Locale defaultLocale) {
        if (executorType == Executor.embedded && !defaultLocale.equals(Locale.getDefault())) {
            // need to fork to apply the new default locale
            requireDaemon().requireIsolatedDaemons();
        }
        return super.withDefaultLocale(defaultLocale);
    }
}
