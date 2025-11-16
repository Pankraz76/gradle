/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.internal.scopeids

import org.gradle.integtests.fixtures.CrossVersionIntegrationSpec
import org.gradle.integtests.fixtures.ScopeIdsFixture
import org.gradle.integtests.fixtures.TargetVersions
import org.gradle.integtests.fixtures.executor.GradleExecutor
import org.junit.Rule

@TargetVersions("4.0+")
class CrossVersionScopeIdsIntegrationTest extends CrossVersionIntegrationSpec {

    GradleExecutor currentExecutor = version(current)
    GradleExecutor previousExecutor

    @Rule
    ScopeIdsFixture scopeIds = new ScopeIdsFixture(currentExecutor, temporaryFolder)

    def setup() {
        previousExecutor = version(previous)
        scopeIds.configureExecutor(previousExecutor)
    }

    void runCurrent() {
        currentExecutor.withTasks("help").run()
    }

    void runEarlier() {
        previousExecutor.withTasks("help").run()
    }

    def "reads ids written by earlier versions"() {
        when:
        runEarlier()
        runCurrent()

        then:
        assertIdsAreShared(scopeIds.ids(0), scopeIds.ids(1))
    }

    def "writes ids readable by earlier versions"() {
        when:
        runCurrent()
        runEarlier()

        then:
        assertIdsAreShared(scopeIds.ids(1), scopeIds.ids(0))
    }

    void assertIdsAreShared(ScopeIdsFixture.ScopeIds earlier, ScopeIdsFixture.ScopeIds current) {
        assert current.buildInvocation != earlier.buildInvocation
        assert current.workspace == earlier.workspace
        assert current.user == earlier.user
    }

}
