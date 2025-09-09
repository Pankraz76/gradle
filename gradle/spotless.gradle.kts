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

plugins {
    id("com.diffplug.spotless")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        // removeWildcardImports() resolve with #link, but rewrite currently not scaling.
        // endWithNewline()
        // trimTrailingWhitespace()
        removeUnusedImports()
        target("**/*.java")
        targetExclude(
            "platforms/documentation/**",
            "platforms/enterprise/enterprise-plugin-performance/src/templates/**",
            "platforms/jvm/language-groovy/src/testFixtures/resources/**",
            "testing/performance/src/templates/**"
        )
    }
}

tasks.check {
    dependsOn(tasks.spotlessCheck)
}

repositories {
    mavenCentral()
}

// check.dependsOn(spotlessCheck)
