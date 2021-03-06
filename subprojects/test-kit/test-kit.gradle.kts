/*
 * Copyright 2019 the original author or authors.
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
import org.gradle.gradlebuild.test.integrationtests.getIncludeCategories
import org.gradle.gradlebuild.testing.integrationtests.cleanup.WhenNotEmpty

plugins {
    gradlebuild.distribution.`core-api-java`
}

dependencies {
    implementation(project(":baseServices"))
    implementation(project(":coreApi"))
    implementation(project(":core"))
    implementation(project(":wrapper"))
    implementation(project(":toolingApi"))
    implementation(library("commons_io"))

    runtimeOnly(project(":native"))

    testImplementation(library("guava"))
    testImplementation(testFixtures(project(":core")))

    integTestImplementation(project(":native"))
    integTestImplementation(project(":logging"))
    integTestImplementation(project(":launcher"))
    integTestImplementation(project(":buildOption"))
    integTestImplementation(project(":jvmServices"))
    integTestImplementation(library("slf4j_api"))
    integTestRuntimeOnly(project(":toolingApiBuilders"))
    integTestRuntimeOnly(project(":pluginDevelopment"))
    integTestRuntimeOnly(project(":runtimeApiInfo"))
    integTestRuntimeOnly(project(":testingJunitPlatform"))
}

classycle {
    excludePatterns.set(listOf("org/gradle/testkit/runner/internal/**"))
}

tasks.integMultiVersionTest {
    systemProperty("org.gradle.integtest.testkit.compatibility", "all")
    // TestKit multi version tests are not using JUnit categories
    getIncludeCategories().clear()
}

testFilesCleanup {
    policy.set(WhenNotEmpty.REPORT)
}
