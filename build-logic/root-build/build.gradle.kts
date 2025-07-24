plugins {
    id("gradlebuild.build-logic.kotlin-dsl-gradle-plugin")
    id("org.openrewrite.rewrite") version("7.12.1")
}

description = "Provides plugins that configures the root Gradle project"

dependencies {
    implementation("gradlebuild:basics")

    implementation(projects.idea)
    implementation(projects.profiling)

    implementation(projects.cleanup) {
        because("The CachesCleaner service is shared and needs to be on the root classpath")
    }

    implementation("com.autonomousapps:dependency-analysis-gradle-plugin")
    rewrite(
        "org.openrewrite.recipe:rewrite-static-analysis:2.12.0",
        "org.openrewrite.recipe:rewrite-third-party:0.24.3",
        "org.openrewrite.recipe:rewrite-static-analysis:2.13.0",
        "org.openrewrite.recipe:rewrite-migrate-java:3.13.0"
    )
}

rewrite {
    activeRecipe(
        "org.openrewrite.gradle.GradleBestPractices",
        "org.openrewrite.java.RemoveUnusedImports",
        "org.openrewrite.java.format.RemoveTrailingWhitespace",
        "org.openrewrite.staticanalysis.RemoveUnusedPrivateMethods",
        "org.openrewrite.text.EndOfLineAtEndOfFile",
        //"org.openrewrite.java.migrate.util.MigrateCollectionsSingletonList",
        //"org.openrewrite.java.migrate.util.MigrateCollectionsUnmodifiableList",
        "tech.picnic.errorprone.refasterrules.AssertJStringRulesRecipes"
    )
    failOnDryRunResults = true
}
