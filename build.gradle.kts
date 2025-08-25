plugins {
    id("gradlebuild.root-build")

    id("gradlebuild.teamcity-import-test-data")  // CI: Import Test tasks' JUnit XML if they're UP-TO-DATE or FROM-CACHE
    id("gradlebuild.lifecycle")                  // CI: Add lifecycle tasks to for the CI pipeline (currently needs to be applied early as it might modify global properties)
    id("gradlebuild.generate-subprojects-info")  // CI: Generate subprojects information for the CI testing pipeline fan out
    id("gradlebuild.cleanup")                    // CI: Advanced cleanup after the build (like stopping daemons started by tests)

    id("gradlebuild.update-versions")            // Local development: Convenience tasks to update versions in this build: 'released-versions.json', 'agp-versions.properties', ...
    id("gradlebuild.wrapper")                    // Local development: Convenience tasks to update the wrapper (like 'nightlyWrapper')
//    id("org.openrewrite.rewrite") version "7.14.1"
    id("org.openrewrite.rewrite") version "7.13.0"
    id("pmd")
}

repositories {
    mavenCentral()
}

description = "Adaptable, fast automation for all"

rewrite {
    activeRecipe("org.gradle.openrewrite.CodeCleanupComposition")
    exclusions.add("**.GarbageCollectedMemoryPoolTest.java")
    exclusions.add("**.MetadataVersionTest.java")
    setExportDatatables(true)
    setFailOnDryRunResults(true)
}

pmd {
    ruleSetFiles = files("custom-pmd-ruleset.xml")
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-static-analysis:latest.release")
    rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:latest.release"))
}

tasks {
    check {
        dependsOn(rewriteDryRun)
    }
}
