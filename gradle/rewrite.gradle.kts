/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

plugins {
    id("com.diffplug.spotless")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        // removeWildcardImports() resolve with #link, but rewrite currently not scaling.
        endWithNewline()
        trimTrailingWhitespace()
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
