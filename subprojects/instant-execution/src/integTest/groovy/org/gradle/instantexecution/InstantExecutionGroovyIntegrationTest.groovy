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

package org.gradle.instantexecution


import static org.gradle.integtests.fixtures.RepoScriptBlockUtil.jcenterRepository


class InstantExecutionGroovyIntegrationTest extends AbstractInstantExecutionIntegrationTest {

    def setup() {
        executer.noDeprecationChecks()
    }

    def "build on Groovy build with JUnit tests"() {

        def instantExecution = newInstantExecutionFixture()

        given:
        buildFile << """
            plugins { id 'groovy' }
        
            repositories {
                ${jcenterRepository()}
            }
            
            dependencies {
                implementation(localGroovy())
                testImplementation("junit:junit:4.12")
            }
        """
        file("src/main/groovy/Thing.groovy") << """
            class Thing {}
        """
        file("src/test/groovy/ThingTest.groovy") << """
            import org.junit.*
            class ThingTest {
                @Test void ok() { new Thing() }
            } 
        """

        and:
        def expectedTasks = [
            ":compileJava", ":compileGroovy", ":processResources", ":classes", ":jar", ":assemble",
            ":compileTestJava", ":compileTestGroovy", ":processTestResources", ":testClasses", ":test", ":check",
            ":build"
        ]
        def classFile = file("build/classes/groovy/main/Thing.class")
        def testClassFile = file("build/classes/groovy/test/ThingTest.class")
        def testResults = file("build/test-results/test")

        when:
        instantRun "build"

        then:
        instantExecution.assertStateStored()
        result.assertTasksExecuted(*expectedTasks)

        and:
        classFile.isFile()
        testClassFile.isFile()
        testResults.isDirectory()

        and:
        assertTestsExecuted("ThingTest", "ok")

        when:
        classFile.delete()
        testClassFile.delete()
        testResults.delete()

        and:
        instantRun "build"

        then:
        instantExecution.assertStateLoaded()
        result.assertTasksExecuted(*expectedTasks)

        and:
        classFile.isFile()
        testClassFile.isFile()
        testResults.isDirectory()

        and:
        assertTestsExecuted("ThingTest", "ok")
    }
}