import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Luminara - Pinnacl Project.
 * @author Luminara Team
 *
 * JetBrains Space Automation
 * This Kotlin-script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */
val projectName = "pinnacl-academics"
val luminaraChartRepo = "luminara.registry.jetbrains.space/p/pi/pinnacl-hub"
val luminaraRegistry = "oci://$luminaraChartRepo"
val containerNameVersion = "gradle-helm-ci:jdk21-8.7-3.12.3"
val containerImage = "$luminaraChartRepo/luminara/$containerNameVersion"
val chartName = projectName

// Define Space Channel where CI/CD notifications are sent
val recipient = ChannelIdentifier.Channel(ChatChannel.FromName("CI-CD-Notifications"))

// Google Jib - Gradle task CLI override arguments
val jibToImage = "-Djib.to.image=luminara.registry.jetbrains.space/p/pi/pinnacl-hub/luminara/${projectName}:latest"
val jibToAuthUsername = "-Djib.to.auth.username=" + System.getenv("JB_SPACE_CLIENT_ID")
val jibToAuthPassword = "-Djib.to.auth.password=" + System.getenv("JB_SPACE_CLIENT_SECRET")

suspend fun executeTask(
    api: circlet.pipelines.script.ScriptApi,
    stepName: String,
    stepIndex: Int,
    maxIndex: Int,
    stepDesc: String,
    useGradlew: Boolean = false,
    vararg gradleTaskArgs: String
) {
    api.space().chats.messages.sendMessage(
        channel = recipient,
        content = ChatMessage.Text("**Step $stepIndex**: `$stepName` on branch: `${api.gitBranch()}` at _${buildTime()}_")
    )
    try {
        val startTime = LocalDateTime.now()
        api.space().chats.messages.sendMessage(
            channel = recipient,
            content = ChatMessage.Text("- _TASK_:`$stepDesc` **[$stepIndex/$maxIndex]** STARTED at _${buildTime(startTime)}_")
        )
        if (useGradlew) {
            api.gradlew(*gradleTaskArgs)
        } else {
            api.gradle(*gradleTaskArgs)
        }

        val endTime = LocalDateTime.now()
        api.space().chats.messages.sendMessage(
            channel = recipient,
            content = ChatMessage.Text(
                "- _TASK_:`$stepDesc` **[$stepIndex/$maxIndex]** COMPLETED at _${buildTime(startTime)}_ and took _${
                    Duration.between(
                        startTime.toInstant(ZoneOffset.UTC),
                        endTime.toInstant(ZoneOffset.UTC)
                    ).toMinutes()
                } minutes_"
            )
        )
    } catch (ex: Exception) {
        api.space().chats.messages.sendMessage(
            channel = recipient, content = ChatMessage.Text("- _TASK_:`$stepDesc` **[$stepIndex/$maxIndex]** FAILED at _${buildTime()}_")
        )
        throw ex
    }
}

fun buildTime(dateTime: LocalDateTime = LocalDateTime.now()): String {
    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

suspend fun jobStarted(
    api: circlet.pipelines.script.ScriptApi,
    jobName: String
) {
    api.space().chats.messages.sendMessage(
        channel = recipient,
        content = ChatMessage.Text(
            "**PINNACL | ${api.gitRepositoryName()}**" +
                    "\n **JOB _${jobName}_ (Build: ${api.executionNumber()}) STARTED at ${buildTime()}**"
        )
    )
}

suspend fun jobEnded(
    api: circlet.pipelines.script.ScriptApi,
    jobName: String
) {
    api.space().chats.messages.sendMessage(
        channel = recipient,
        content = ChatMessage.Text(
            "**PINNACL | ${api.gitRepositoryName()}**" +
                    "\n **JOB _${jobName}_ (Build: ${api.executionNumber()}) COMPLETED at ${buildTime()}**"
        )
    )
}

suspend fun gradleHelmCompleted(
    api: circlet.pipelines.script.ScriptApi,
    stepIndex: Int,
    maxIndex: Int,
    stepDesc: String
) {
    api.space().chats.messages.sendMessage(
        channel = recipient,
        content = ChatMessage.Text("- _TASK_:`$stepDesc` **[$stepIndex/$maxIndex]** COMPLETED at _${buildTime()}_")
    )
}

job("Branch: Compile, Test & Publish (SNAPSHOT)") {
    val totalSteps = 3

    startOn {
        gitPush {
            enabled = true
            branchFilter {
                +"refs/heads/pi-t-*"
            }
        }
    }

    git {
        refSpec {
            +"refs/tags/*:refs/tags/*"
        }
        // get all commits for the main repo
        depth = UNLIMITED_DEPTH
    }

    container(displayName = "Gradle - Compile & Test", image = containerImage) {
        kotlinScript { api ->
            jobStarted(api, "Branch: Build")
            executeTask(
                api,
                "compile",
                1,
                totalSteps,
                "`gradle test`",
                false,
                "-PbuildProfile=space",
                "test"
            )
        }
    }


    container(displayName = "Gradle - Package & Publish (Jar & Jib)", image = containerImage) {
        kotlinScript { api ->
            executeTask(
                api,
                "publish",
                2,
                totalSteps,
                "`gradle publish`",
                false,
                "-PbuildProfile=space",
                "-PsnapshotBuild=true",
                "semverCompute",
                "publish",
                "jib",
                jibToImage,
                jibToAuthUsername,
                jibToAuthPassword
            )
        }
    }


    container(displayName = "Gradle - Package & Publish (Helm)", image = containerImage) {
        shellScript {
            content = """
                export HELM_EXPERIMENTAL_OCI=1
                export GRADLE_PROJECT_VERSION=${'$'}(./gradlew -PbuildProfile=space -PsnapshotBuild=true semverCompute printVersion | grep "projectVersion=>" | awk '{print ${'$'}2}')
                docker login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the docker registry'
                echo '====================================================================='
                helm registry login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the helm registry ======'
                echo '====================================================================='
                gradle -PbuildProfile=space -PsnapshotBuild=true semverCompute helmPackage
                echo '====== Gradle Helm Package completed ======'
                echo '====================================================================='
                export CHART_PATH=$mountDir/work/$projectName/build/helm/charts/$chartName-${'$'}GRADLE_PROJECT_VERSION.tgz
                helm push ${'$'}CHART_PATH $luminaraRegistry
                echo '====== Helm push to registry completed ======'
                echo '====================================================================='
            """
        }
    }


    container(displayName = "Notification - Gradle Helm Plugin", image = containerImage) {
        kotlinScript { api ->
            gradleHelmCompleted(api, 3, totalSteps, "`gradle helmPackage`")
            jobEnded(api, "Branch: Compile, Test & Publish (SNAPSHOT)")
        }
    }
}


job("Main: Build & Publish") {
    val totalSteps = 5

    startOn {
        gitPush {
            enabled = true
            branchFilter {
                +"refs/heads/main"
            }
        }
    }

    git {
        refSpec {
            +"refs/tags/*:refs/tags/*"
        }
        depth = UNLIMITED_DEPTH
    }

    container(displayName = "Gradle - Assemble", image = containerImage) {
        kotlinScript { api ->
            jobStarted(api, "Main: Build and Publish Artifacts")
            executeTask(
                api,
                "assemble",
                1,
                totalSteps,
                "`gradle assemble`",
                false,
                "-PbuildProfile=space",
                "-xhelmUpdateMainChartDependencies", "-xhelmPackageMainChart",
                "assemble"
            )
        }
    }


    container(displayName = "Gradle - Check", image = containerImage) {
        kotlinScript { api ->
            executeTask(
                api,
                "check",
                2,
                totalSteps,
                "`gradle check`",
                false,
                "-PbuildProfile=space",
                "check"
            )
        }
    }


    container(displayName = "Gradle - Publish", image = containerImage) {
        kotlinScript { api ->
            executeTask(
                api,
                "publish",
                3,
                totalSteps,
                "`gradle semverCompute publish`",
                false,
                "-PbuildProfile=space",
                "semverCompute",
                "publish"
            )
        }
    }
    

    container(displayName = "Gradle - Jib", image = containerImage) {
        kotlinScript { api ->
            executeTask(
                api,
                "jib",
                4,
                totalSteps,
                "`gradle jib`",
                false,
                "-PbuildProfile=space",
                "semverCompute",
                "jib",
                jibToImage,
                jibToAuthUsername,
                jibToAuthPassword
            )
            jobEnded(api, "Main: Build and Publish Artifacts")
        }
    }


    container(displayName = "Gradle - Package & Publish (Helm)", image = containerImage) {
        shellScript {
            content = """
                export HELM_EXPERIMENTAL_OCI=1
                export GRADLE_PROJECT_VERSION=${'$'}(./gradlew -PbuildProfile=space semverCompute printVersion | grep "projectVersion=>" | awk '{print ${'$'}2}')
                docker login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the docker registry'
                echo '====================================================================='
                helm registry login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the helm registry ======'
                echo '====================================================================='
                gradle -PbuildProfile=space semverCompute helmPackage
                echo '====== Gradle Helm Package completed ======'
                echo '====================================================================='
                export CHART_PATH=$mountDir/work/$projectName/build/helm/charts/$chartName-${'$'}GRADLE_PROJECT_VERSION.tgz
                helm push ${'$'}CHART_PATH $luminaraRegistry
                echo '====== Helm push to registry completed ======'
                echo '====================================================================='
            """
        }
    }


    container(displayName = "Notification - Gradle Helm Plugin", image = containerImage) {
        kotlinScript { api ->
            gradleHelmCompleted(api, 5, totalSteps, "`gradle helmPackage`")
            jobEnded(api, "Main: Build & Publish")
        }
    }
}


job("Main: Publish Artifacts") {
    val totalSteps = 2

    startOn {
        gitPush {
            enabled = false
            branchFilter {
                +"refs/heads/main"
            }
        }
    }

    git {
        refSpec {
            +"refs/tags/*:refs/tags/*"
        }
        depth = UNLIMITED_DEPTH
    }

    container(displayName = "Gradle - Jib", image = containerImage) {
        kotlinScript { api ->
            executeTask(
                api,
                "jib",
                1,
                totalSteps,
                "`gradle jib`",
                false,
                "-PbuildProfile=space",
                "semverCompute",
                "jib",
                jibToImage,
                jibToAuthUsername,
                jibToAuthPassword
            )
        }
    }

    container(displayName = "Gradle - Package & Publish (Helm)", image = containerImage) {
        shellScript {
            content = """
                #!/usr/bin/env bash
                # Exit with a non-zero status if any of the following commands fail 
                # (for reference see: https://www.gnu.org/software/bash/manual/html_node/The-Set-Builtin.html)
                set -e
                export HELM_EXPERIMENTAL_OCI=1
                export GRADLE_PROJECT_VERSION=${'$'}(./gradlew -PbuildProfile=space semverCompute printVersion | grep "projectVersion=>" | awk '{print ${'$'}2}')
                docker login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the docker registry'
                echo '====================================================================='
                helm registry login luminara.registry.jetbrains.space -u ${'$'}JB_SPACE_CLIENT_ID -p ${'$'}JB_SPACE_CLIENT_SECRET
                echo '====== We are logged into the helm registry ======'
                echo '====================================================================='
                gradle -PbuildProfile=space semverCompute helmPackage
                echo '====== Gradle Helm Package completed ======'
                echo '====================================================================='
                export CHART_PATH=$mountDir/work/$projectName/build/helm/charts/$chartName-${'$'}GRADLE_PROJECT_VERSION.tgz
                helm push ${'$'}CHART_PATH $luminaraRegistry
                echo '====== Helm push to registry completed ======'
                echo '====================================================================='
            """
        }
    }


    container(displayName = "Notification - Gradle Helm Plugin", image = containerImage) {
        kotlinScript { api ->
            gradleHelmCompleted(api, 2, totalSteps, "`gradle helmPackage`")
            jobEnded(api, "Main: Publish Artifacts")
        }
    }
}