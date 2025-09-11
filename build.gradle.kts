plugins {
    id("gradlebuild.root-build")

    id("gradlebuild.teamcity-import-test-data")  // CI: Import Test tasks' JUnit XML if they're UP-TO-DATE or FROM-CACHE
    id("gradlebuild.lifecycle")                  // CI: Add lifecycle tasks to for the CI pipeline (currently needs to be applied early as it might modify global properties)
    id("gradlebuild.generate-subprojects-info")  // CI: Generate subprojects information for the CI testing pipeline fan out
    id("gradlebuild.cleanup")                    // CI: Advanced cleanup after the build (like stopping daemons started by tests)

    id("gradlebuild.update-versions")            // Local development: Convenience tasks to update versions in this build: 'released-versions.json', 'agp-versions.properties', ...
    id("gradlebuild.wrapper")                    // Local development: Convenience tasks to update the wrapper (like 'nightlyWrapper')
    id("com.diffplug.spotless") version "7.2.1" apply false
    id("org.openrewrite.rewrite") version "7.15.0" apply false
    id("org.owasp.dependencycheck") version "12.1.3" apply false
}

description = "Adaptable, fast automation for all"
