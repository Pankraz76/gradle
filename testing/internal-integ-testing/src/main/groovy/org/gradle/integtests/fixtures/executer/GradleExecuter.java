/*
 * Copyright 2009 the original author or authors.
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

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.logging.configuration.ConsoleOutput;
import org.gradle.api.logging.configuration.WarningMode;
import org.gradle.integtests.fixtures.RichConsoleStyling;
import org.gradle.internal.concurrent.Stoppable;
import org.gradle.internal.jvm.Jvm;
import org.gradle.test.fixtures.file.TestDirectoryProvider;
import org.gradle.test.fixtures.file.TestFile;
import org.gradle.util.GradleVersion;
import org.gradle.util.internal.TextUtil;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface GradleExecutor extends Stoppable {

    /**
     * Sets the working directory to use. Defaults to the test's temporary directory.
     */
    GradleExecutor inDirectory(File directory);

    /**
     * Sets the task names to execute. Defaults to an empty list.
     */
    GradleExecutor withTasks(String... names);

    /**
     * Sets the task names to execute. Defaults to an empty list.
     */
    GradleExecutor withTasks(List<String> names);

    GradleExecutor withTaskList();

    GradleExecutor withDependencyList();

    GradleExecutor withQuietLogging();

    /**
     * Sets the additional command-line arguments to use when executing the build. Defaults to an empty list.
     */
    GradleExecutor withArguments(String... args);

    /**
     * Sets the additional command-line arguments to use when executing the build. Defaults to an empty list.
     */
    GradleExecutor withArguments(List<String> args);

    /**
     * Adds an additional command-line argument to use when executing the build.
     */
    GradleExecutor withArgument(String arg);

    /**
     * Sets the additional environment variables to use when executing the build.
     * <p>
     * The provided environment is added to the environment variables of this process, so it is only possible to add new variables or modify values of existing ones.
     * Not propagating a variable of this process to the executed build at all is not supported.
     * <p>
     * Setting "JAVA_HOME" this way is not supported.
     */
    GradleExecutor withEnvironmentVars(Map<String, ?> environment);

    /**
     * Sets the additional environment variables to use when executing the build, allowing to pass JAVA_HOME as well.
     * <p>
     * The provided environment is added to the environment variables of this process, so it is only possible to add new variables or modify values of existing ones.
     * Not propagating a variable of this process to the executed build at all is not supported.
     * <p>
     * Setting "JAVA_HOME" this way is not supported.
     */
    GradleExecutor withEnvironmentVarsIncludingJavaHome(Map<String, ?> environment);

    GradleExecutor usingInitScript(File initScript);

    /**
     * Uses the given project directory
     */
    GradleExecutor usingProjectDirectory(File projectDir);

    /**
     * Sets the user's home dir to use when running the build. Implementations are not 100% accurate.
     */
    GradleExecutor withUserHomeDir(File userHomeDir);

    /**
     * Sets the <em>Gradle</em> user home dir. Setting to null requests that the executor use the real default Gradle user home dir rather than the default used for testing.
     *
     * This value is persistent across executions by this executor.
     *
     * <p>Note: does not affect the daemon base dir.</p>
     */
    GradleExecutor withGradleUserHomeDir(File userHomeDir);

    /**
     * Sets the Gradle version for executing Gradle.
     *
     * This does not actually use a different gradle version,
     * it just modifies result of DefaultGradleVersion.current() for the Gradle that is run by the executor.
     */
    GradleExecutor withGradleVersionOverride(GradleVersion gradleVersion);

    /**
     * Sets the java home dir. Replaces any value set by {@link #withJvm(Jvm)}.
     * <p>
     * In general, prefer using {@link #withJvm(Jvm)} over this method. This method should be used
     * when testing non-standard JVMs, like embedded JREs, or those not provided by
     * {@link org.gradle.integtests.fixtures.AvailableJavaHomes}.
     */
    GradleExecutor withJavaHome(String userHomeDir);

    /**
     * Sets the JVM to execute Gradle with. Replaces any value set by {@link #withJavaHome(String)}.
     *
     * @throws IllegalArgumentException If the given JVM is not probed, for example JVMs created by {@link Jvm#forHome(File)}
     */
    GradleExecutor withJvm(Jvm jvm);

    /**
     * Sets the executable to use. Set to null to use the real default executable (if any) rather than the default used for testing.
     */
    GradleExecutor usingExecutable(String script);

    /**
     * Sets a stream to use for writing to stdin which can be retrieved with getStdinPipe().
     */
    GradleExecutor withStdinPipe();

    /**
     * Sets a stream to use for writing to stdin.
     */
    GradleExecutor withStdinPipe(PipedOutputStream stdInPipe);

    default GradleExecutor withStdIn(String input) {
        return withStdinPipe(new PipedOutputStream() {
            @Override
            public void connect(PipedInputStream snk) throws IOException {
                super.connect(snk);
                write(TextUtil.toPlatformLineSeparators(input).getBytes());
            }
        });
    }

    /**
     * Executes the requested build, asserting that the build succeeds. Resets the configuration of this executor.
     *
     * @return The result.
     */
    ExecutionResult run();

    /**
     * Executes the requested build, asserting that the build fails. Resets the configuration of this executor.
     *
     * @return The result.
     */
    ExecutionFailure runWithFailure();

    /**
     * Starts executing the build asynchronously.
     *
     * @return the handle, never null.
     */
    GradleHandle start();

    /**
     * Adds JVM args that should be used to start any command-line `gradle` executable used to run the build. Note that this may be different to the build JVM, for example the build may run in a
     * daemon process. You should prefer using {@link #withBuildJvmOpts(String...)} over this method.
     */
    GradleExecutor withCommandLineGradleOpts(String... jvmOpts);

    /**
     * See {@link #withCommandLineGradleOpts(String...)}.
     */
    GradleExecutor withCommandLineGradleOpts(Iterable<String> jvmOpts);

    /**
     * Adds JVM args that should be used by the build JVM. Does not necessarily imply that the build will be run in a separate process, or that a new build JVM will be started, only that the build
     * will run in a JVM that was started with the specified args.
     *
     * @param jvmOpts the JVM opts
     * @return this executor
     */
    GradleExecutor withBuildJvmOpts(String... jvmOpts);

    /**
     * See {@link #withBuildJvmOpts(String...)}.
     */
    GradleExecutor withBuildJvmOpts(Iterable<String> jvmOpts);

    /**
     * Activates the build cache
     *
     * @return this executor
     */
    GradleExecutor withBuildCacheEnabled();

    /**
     * Activates the configuration cache
     *
     * @return this executor
     */
    GradleExecutor withConfigurationCacheEnabled();

    /**
     * Don't set native services dir explicitly.
     */
    GradleExecutor withNoExplicitNativeServicesDir();

    /**
     * Enables the rendering of stack traces for deprecation logging.
     */
    GradleExecutor withFullDeprecationStackTraceEnabled();

    GradleExecutor withoutInternalDeprecationStackTraceFlag();

    /**
     * Downloads and sets up the JVM arguments for running the Gradle daemon with the file leak detector: https://github.com/jenkinsci/lib-file-leak-detector
     *
     * NOTE: This requires running the test with at least JDK8 and the forking executor. This will apply the file leak detection version suitable for executor Java version.
     * If your build sets a different Java version you can use {@link #withFileLeakDetection(JavaVersion, String...)} to specify the Java version for which the file leak detection should be enabled.
     *
     * This should not be checked-in on. This is only for local debugging.
     *
     * By default, this starts a HTTP server on port 19999, so you can observe which files are open on http://localhost:19999. Passing any arguments disables this behavior.
     *
     * @param args the arguments to pass the file leak detector java agent
     */
    GradleExecutor withFileLeakDetection(String... args);

    /**
     * Same as {@link #withFileLeakDetection(String...)}, but allows to specify the Java version for which the file leak detection should be enabled.
     */
    GradleExecutor withFileLeakDetection(JavaVersion javaVersion, String... args);

    /**
     * Specifies that the executor should only those JVM args explicitly requested using {@link #withBuildJvmOpts(String...)} and {@link #withCommandLineGradleOpts(String...)} (where appropriate) for
     * the build JVM and not attempt to provide any others.
     */
    GradleExecutor useOnlyRequestedJvmOpts();

    /**
     * Sets the default character encoding to use.
     *
     * Only makes sense for forking executors.
     *
     * @return this executor
     */
    GradleExecutor withDefaultCharacterEncoding(String defaultCharacterEncoding);

    /**
     * Sets the default locale to use.
     *
     * Only makes sense for forking executors.
     *
     * @return this executor
     */
    GradleExecutor withDefaultLocale(Locale defaultLocale);

    /**
     * Set the number of seconds an idle daemon should live for.
     *
     * @return this executor
     */
    GradleExecutor withDaemonIdleTimeoutSecs(int secs);

    /**
     * Set the working space for any daemons used by the builds.
     *
     * This value is persistent across executions by this executor.
     *
     * <p>Note: this does not affect the Gradle user home directory.</p>
     *
     * @return this executor
     */
    GradleExecutor withDaemonBaseDir(File baseDir);

    /**
     * Sets the path to the read-only dependency cache
     *
     * @param cacheDir the path to the RO dependency cache
     * @return this executor
     */
    GradleExecutor withReadOnlyCacheDir(File cacheDir);

    /**
     * Sets the path to the read-only dependency cache
     *
     * @param cacheDir the path to the RO dependency cache
     * @return this executor
     */
    GradleExecutor withReadOnlyCacheDir(String cacheDir);

    /**
     * Returns the working space for any daemons used by the builds.
     */
    File getDaemonBaseDir();

    /**
     * Requires that the build run in a separate daemon process.
     */
    GradleExecutor requireDaemon();

    /**
     * Asserts that this executor will be able to run a build, given its current configuration.
     *
     * @throws AssertionError When this executor will not be able to run a build.
     */
    void assertCanExecute() throws AssertionError;

    /**
     * Adds an action to be called immediately before execution, to allow extra configuration to be injected.
     */
    void beforeExecute(Action<? super GradleExecutor> action);

    /**
     * Adds an action to be called immediately before execution, to allow extra configuration to be injected.
     */
    void beforeExecute(@DelegatesTo(GradleExecutor.class) Closure action);

    /**
     * Adds an action to be called immediately after execution
     */
    void afterExecute(Action<? super GradleExecutor> action);

    /**
     * Adds an action to be called immediately after execution
     */
    void afterExecute(@DelegatesTo(GradleExecutor.class) Closure action);

    /**
     * The directory that the executor will use for any test specific storage.
     *
     * May or may not be the same directory as the build to be run.
     */
    TestDirectoryProvider getTestDirectoryProvider();

    /**
     * Expects the given deprecation warning.
     *
     * @implNote URLs to documentation should use /current/ as the version. This fixture will automatically replace it with the actual version tested.
     */
    GradleExecutor expectDocumentedDeprecationWarning(String warning);

    /**
     * Do not call this method directly.
     *
     * @see #expectDocumentedDeprecationWarning(String)
     */
    GradleExecutor expectDeprecationWarning(ExpectedDeprecationWarning warning);

    /**
     * Disable deprecation warning checks.
     */
    GradleExecutor noDeprecationChecks();

    /**
     * Expects a message that contains the word "deprecated" in the output.
     *
     * This is not intended to test for Gradle deprecation warnings. Use {@link #expectDocumentedDeprecationWarning(String)} instead.
     *
     * This method is used to document builds that emit deprecation messages from external tools like javac or the Kotlin compiler.
     */
    GradleExecutor expectExternalDeprecatedMessage(String warning);

    /**
     * Disable automatic Java version deprecation warning filtering.
     * <p>
     * By default, the executor will ignore all deprecation warnings related to running a build
     * on a Java version that will no longer be supported in future versions of Gradle. In most
     * cases we do not care about this warning, but when we want to explicitly test that a build
     * does emit this warning, we disable this filter.
     */
    GradleExecutor disableDaemonJavaVersionDeprecationFiltering();

    /**
     * Disable crash daemon checks
     */
    GradleExecutor noDaemonCrashChecks();

    /**
     * Disables asserting that class loaders were not eagerly created, potentially leading to performance problems.
     */
    GradleExecutor withEagerClassLoaderCreationCheckDisabled();

    /**
     * Disables asserting that no unexpected stacktraces are present in the output.
     */
    GradleExecutor withStackTraceChecksDisabled();

    /**
     * Enables checks for warnings emitted by the JDK itself. Including illegal access warnings.
     */
    GradleExecutor withJdkWarningChecksEnabled();

    /**
     * An executor may decide to implicitly bump the logging level, unless this is called.
     */
    GradleExecutor noExtraLogging();

    /**
     * Configures that any daemons used by the execution are unique to the test.
     *
     * This value is persistent across executions by this executor.
     *
     * <p>Note: this does not affect the Gradle user home directory.</p>
     */
    GradleExecutor requireIsolatedDaemons();

    /**
     * Disable worker daemons expiration.
     */
    GradleExecutor withWorkerDaemonsExpirationDisabled();

    /**
     * Returns true if this executor will share daemons with other executors.
     */
    boolean usesSharedDaemons();

    /**
     * Use {@link #requireOwnGradleUserHomeDir(String because)} instead.
     */
    @Deprecated
    GradleExecutor requireOwnGradleUserHomeDir();

    /**
     * Configures a unique gradle user home dir for the test.
     *
     * The gradle user home dir used will be underneath the {@link #getTestDirectoryProvider()} directory.
     *
     * This value is persistent across executions by this executor.
     *
     * <p>Note: does not affect the daemon base dir.</p>
     */
    GradleExecutor requireOwnGradleUserHomeDir(String because);

    /**
     * The Gradle user home dir that will be used for executions.
     */
    TestFile getGradleUserHomeDir();

    /**
     * The distribution used to execute.
     */
    GradleDistribution getDistribution();

    /**
     * Copies the settings from this executor to the given executor.
     *
     * @param executor The executor to copy to
     * @return The passed in executor
     */
    GradleExecutor copyTo(GradleExecutor executor);

    /**
     * Where possible, starts the Gradle build process in debug mode with the provided options.
     */
    GradleExecutor startBuildProcessInDebugger(Action<JavaDebugOptionsInternal> action);

    /**
     * Where possible, starts the Gradle build process in suspended debug mode.
     */
    GradleExecutor startBuildProcessInDebugger(boolean flag);

    GradleExecutor withProfiler(String profilerArg);

    /**
     * Forces Gradle to consider the build to be interactive
     */
    GradleExecutor withForceInteractive(boolean flag);

    boolean isDebug();

    boolean isProfile();

    /**
     * Starts the launcher JVM (daemon client) in suspended debug mode
     */
    GradleExecutor startLauncherInDebugger(boolean debugLauncher);

    /**
     * Starts the launcher JVM (daemon client) in debug mode with the provided options
     */
    GradleExecutor startLauncherInDebugger(Action<JavaDebugOptionsInternal> action);

    boolean isDebugLauncher();

    /**
     * Clears previous settings so that instance can be reused
     */
    GradleExecutor reset();

    /**
     * Measures the duration of the execution
     */
    GradleExecutor withDurationMeasurement(DurationMeasurement durationMeasurement);

    /**
     * Returns true if this executor uses a daemon
     */
    boolean isUseDaemon();

    /**
     * Configures that user home services should not be reused across multiple invocations.
     *
     * <p>
     * Note: You will want to call this method if the test case defines a custom Gradle user home directory
     * so the services can be shut down after test execution in
     * {@link org.gradle.internal.service.scopes.DefaultGradleUserHomeScopeServiceRegistry#release(org.gradle.internal.service.ServiceRegistry)}.
     * Not calling the method in those situations will result in the inability to delete a file lock.
     * </p>
     */
    GradleExecutor withOwnUserHomeServices();

    /**
     * Executes the build with {@code "--console=rich, auto, verbose"} argument.
     *
     * @see RichConsoleStyling
     */
    GradleExecutor withConsole(ConsoleOutput consoleOutput);

    /**
     * Executes the build with {@code "--warning-mode=none, summary, fail, all"} argument.
     *
     * @see WarningMode
     */
    GradleExecutor withWarningMode(WarningMode warningMode);

    /**
     * Execute the builds with adding the {@code "--stacktrace"} argument.
     */
    GradleExecutor withStacktraceEnabled();

    /**
     * Renders the welcome message users see upon first invocation of a Gradle distribution with a given Gradle user home directory.
     * By default the message is never rendered.
     */
    GradleExecutor withWelcomeMessageEnabled();

    /**
     * Specifies we should use a test console that has both stdout and stderr attached.
     */
    GradleExecutor withTestConsoleAttached();

    /**
     * Specifies we should use a test console that only has stdout attached.
     */
    GradleExecutor withTestConsoleAttached(ConsoleAttachment consoleAttachment);

    /**
     * Apply an init script which replaces all external repositories with inner mirrors.
     * Note this doesn't work for buildSrc and composite build.
     *
     * @see org.gradle.integtests.fixtures.RepoScriptBlockUtil
     */
    GradleExecutor withRepositoryMirrors();

    /**
     * Requires an isolated gradle user home and put an init script which replaces all external repositories with inner mirrors.
     * This works for all scenarios.
     *
     * @see org.gradle.integtests.fixtures.RepoScriptBlockUtil
     */
    GradleExecutor withGlobalRepositoryMirrors();

    /**
     * Start the build with {@link org.gradle.api.internal.artifacts.BaseRepositoryFactory#PLUGIN_PORTAL_OVERRIDE_URL_PROPERTY}
     * set to our inner mirror.
     *
     * @see org.gradle.integtests.fixtures.RepoScriptBlockUtil
     */
    GradleExecutor withPluginRepositoryMirrorDisabled();

    GradleExecutor ignoreMissingSettingsFile();

    GradleExecutor ignoreCleanupAssertions();

    GradleExecutor withToolchainDetectionEnabled();

    GradleExecutor withToolchainDownloadEnabled();

}
