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

import com.autonomousapps.DependencyAnalysisExtension

plugins {
    id("gradlebuild.buildscan") // Reporting: Add more data through custom tags to a Build Scan
    id("gradlebuild.ide") // Local development: Tweak IDEA import
    id("gradlebuild.warmup-ec2") // Warm up EC2 AMI

    id("com.autonomousapps.dependency-analysis")
    id("org.openrewrite.rewrite")
}

configure<DependencyAnalysisExtension> {
    issues {
        all {
            onDuplicateClassWarnings {
                severity("fail")
            }
        }
    }

    usage {
        analysis {
            checkSuperClasses(true)
        }
    }

    useTypesafeProjectAccessors(true) // FIXME: has no effect
}

rewrite {
    activeRecipe("org.openrewrite.java.RemoveUnusedImports")
    activeRecipe("org.openrewrite.staticanalysis.EqualsAvoidsNull")
    activeRecipe("org.openrewrite.staticanalysis.ModifierOrder")
    activeRecipe("org.openrewrite.staticanalysis.RemoveUnusedPrivateMethods")
    activeRecipe("org.openrewrite.text.EndOfLineAtEndOfFile")
    configFile = file("config/rewrite.yml")
    exclusions.add("**RunnerWithCustomUniqueIdsAndDisplayNames.java")
    exclusions.add("**SpockTestCaseWithUnrolledAndRegularFeatureMethods.groovy")
    failOnDryRunResults = true
//	activeRecipe("org.junit.openrewrite.recipe.AddLicenseHeader")
//	activeRecipe("org.junit.openrewrite.recipe.Java21ForTests")
//	activeRecipe("org.openrewrite.java.format.WrappingAndBraces")
//	activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
//	activeRecipe("org.openrewrite.java.testing.assertj.Assertj")
//	activeRecipe("org.openrewrite.java.testing.cleanup.AssertTrueNullToAssertNull")
//	activeRecipe("org.openrewrite.java.testing.cleanup.TestsShouldNotBePublic")
//	activeRecipe("org.openrewrite.java.testing.junit5.JUnit5BestPractices")
//	activeRecipe("org.openrewrite.staticanalysis.CodeCleanup") // https://github.com/openrewrite/rewrite-static-analysis/issues/636
//	activeRecipe("org.openrewrite.staticanalysis.CommonStaticAnalysis")
//	activeRecipe("org.openrewrite.staticanalysis.FinalizeLocalVariables")
//	activeRecipe("org.openrewrite.staticanalysis.MissingOverrideAnnotation")
//	activeRecipe("org.openrewrite.staticanalysis.ModifierOrder")
//	activeRecipe("org.openrewrite.staticanalysis.RedundantFileCreation")
//	activeRecipe("org.openrewrite.staticanalysis.RemoveUnusedLocalVariables")
//	activeRecipe("org.openrewrite.staticanalysis.RemoveUnusedPrivateFields")
//	activeRecipe("org.openrewrite.staticanalysis.StringLiteralEquality")
//	setCheckstyleConfigFile(file("config/checkstyleMain.xml")) // https://github.com/openrewrite/rewrite-static-analysis/issues/636
}

dependencies {
    rewrite(platform(dependencyFromLibs("openrewrite-recipe-bom")))
    rewrite("org.openrewrite.recipe:rewrite-migrate-java")
    rewrite("org.openrewrite.recipe:rewrite-testing-frameworks")
}

tasks {
    check {
        dependsOn(rewriteDryRun)
    }
}
