/*
 * Copyright 2012 the original author or authors.
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
package org.gradle.integtests.fixtures;

import org.gradle.api.Action;
import org.gradle.integtests.fixtures.executor.ArtifactBuilder;
import org.gradle.integtests.fixtures.executor.GradleBackedArtifactBuilder;
import org.gradle.integtests.fixtures.executor.GradleContextualExecutor;
import org.gradle.integtests.fixtures.executor.GradleDistribution;
import org.gradle.integtests.fixtures.executor.GradleExecutor;
import org.gradle.integtests.fixtures.executor.InProcessGradleExecutor;
import org.gradle.integtests.fixtures.executor.IntegrationTestBuildContext;
import org.gradle.integtests.fixtures.executor.UnderDevelopmentGradleDistribution;
import org.gradle.test.fixtures.IntegrationTest;
import org.gradle.test.fixtures.dsl.GradleDsl;
import org.gradle.test.fixtures.file.TestFile;
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider;
import org.gradle.test.fixtures.ivy.IvyFileRepository;
import org.gradle.test.fixtures.maven.M2Installation;
import org.gradle.test.fixtures.maven.MavenFileRepository;
import org.gradle.test.precondition.PreconditionVerifier;
import org.junit.After;
import org.junit.Rule;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.util.List;

@IntegrationTest
@Category(IntegrationTest.class)
public abstract class AbstractIntegrationTest implements HasGradleExecutor {

    @Rule
    public final PreconditionVerifier preconditionVerifier = new PreconditionVerifier();

    @Rule
    public final TestNameTestDirectoryProvider testDirectoryProvider = new TestNameTestDirectoryProvider(getClass());

    @Rule
    public final UnsupportedWithConfigurationCacheRule unsupportedWithConfigurationCache = new UnsupportedWithConfigurationCacheRule();

    @Rule
    public final ToBeFixedForConfigurationCacheRule toBeFixedForConfigurationCache = new ToBeFixedForConfigurationCacheRule();

    @Rule
    public final ToBeFixedForIsolatedProjectsRule toBeFixedForIsolatedProjects = new ToBeFixedForIsolatedProjectsRule();

    public final GradleDistribution distribution = new UnderDevelopmentGradleDistribution(getBuildContext());
    public final GradleContextualExecutor executor = createExecutor();

    public IntegrationTestBuildContext getBuildContext() {
        return IntegrationTestBuildContext.INSTANCE;
    }

//    @Rule
    public final M2Installation m2 = new M2Installation(testDirectoryProvider);

    private MavenFileRepository mavenRepo;
    private IvyFileRepository ivyRepo;

    @After
    public void cleanup() {
        executor.cleanup();
    }

    protected GradleContextualExecutor createExecutor() {
        return new GradleContextualExecutor(distribution, testDirectoryProvider, getBuildContext());
    }

    protected GradleDistribution getDistribution() {
        return distribution;
    }

    @Override
    public GradleExecutor getExecutor() {
        return executor;
    }

    protected TestNameTestDirectoryProvider getTestDirectoryProvider() {
        return testDirectoryProvider;
    }

    public TestFile getTestDirectory() {
        return getTestDirectoryProvider().getTestDirectory();
    }

    public TestFile file(Object... path) {
        return getTestDirectory().file(path);
    }

    public TestFile testFile(String name) {
        return file(name);
    }

    public List<TestFile> createDirs(String... names) {
        return getTestDirectory().createDirs(names);
    }

    protected GradleExecutor inTestDirectory() {
        return inDirectory(getTestDirectory());
    }

    protected GradleExecutor inDirectory(File directory) {
        return getExecutor().inDirectory(directory);
    }

    protected GradleExecutor usingProjectDir(File projectDir) {
        return getExecutor().usingProjectDirectory(projectDir);
    }

    protected ArtifactBuilder artifactBuilder() {
        GradleExecutor gradleExecutor = new InProcessGradleExecutor(distribution, testDirectoryProvider);
        gradleExecutor.withGradleUserHomeDir(getExecutor().getGradleUserHomeDir());
        return new GradleBackedArtifactBuilder(gradleExecutor, getTestDirectory().file("artifacts"));
    }

    public MavenFileRepository maven(TestFile repo) {
        return new MavenFileRepository(repo);
    }

    public MavenFileRepository maven(Object repo) {
        return new MavenFileRepository(file(repo));
    }

    public MavenFileRepository getMavenRepo() {
        if (mavenRepo == null) {
            mavenRepo = new MavenFileRepository(file("maven-repo"));
        }
        return mavenRepo;
    }

    public IvyFileRepository ivy(TestFile repo) {
        return new IvyFileRepository(repo);
    }

    public IvyFileRepository ivy(Object repo) {
        return new IvyFileRepository(file(repo));
    }

    public IvyFileRepository getIvyRepo() {
        if (ivyRepo == null) {
            ivyRepo = new IvyFileRepository(file("ivy-repo"));
        }
        return ivyRepo;
    }


    public GradleExecutor using(Action<GradleExecutor> action) {
        action.execute(executor);
        return executor;
    }

    public static String mavenCentralRepository() {
        return mavenCentralRepository(GradleDsl.GROOVY);
    }

    public static String mavenCentralRepository(GradleDsl dsl) {
        return RepoScriptBlockUtil.mavenCentralRepository(dsl);
    }
}
