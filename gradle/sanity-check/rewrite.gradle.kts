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

apply(plugin = "org.openrewrite.rewrite")

//rewrite {
//    activeRecipe("org.gradle.GradleSanityCheck")
//    activeStyle("org.gradle.GradleImportLayout")
//    setExportDatatables(true)
//    setFailOnDryRunResults(true)
//    exclusion(
//        "platforms/documentation/**",
//        "platforms/enterprise/enterprise-plugin-performance/src/templates/**",
//        "platforms/jvm/language-groovy/src/testFixtures/resources/**",
//        "testing/performance/src/templates/**"
//    )
//}

repositories {
    mavenCentral()
}

//dependencies {
//    rewrite("org.openrewrite.recipe:rewrite-static-analysis:2.15.0")
//}
