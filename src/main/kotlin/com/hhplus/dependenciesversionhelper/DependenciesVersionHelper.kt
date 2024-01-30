package com.hhplus.dependenciesversionhelper

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import java.util.regex.Pattern


class DependenciesVersionHelper : ProjectManagerListener {
    /** TODO
     *  [] 프로젝트 오픈 시점
     *     : springboot 버전 읽어서 spring-boot-dependencies maven xml 파일 가져오기 + 파싱
     *  [] build.gradle 파일 오픈 시점 또는 변경이 있을 때
     *     : build.gradle 파일에 있는 dependencies 읽어와서 spring-boot-dependencies 와 비교
     */

    private val connection: MessageBusConnection

    init {
        connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(ProjectManager.TOPIC, this)
    }

    override fun projectOpened(project: Project) {
        // 프로젝트가 열릴 때 실행될 로직
        val springBootVersion = findSpringBootVersion(project)
        println(">>>>> SpringBootVersion: $springBootVersion")
        springBootVersion?.let { retrieveSpringBootDependencies(it) }
    }

    fun dispose() {
        connection.disconnect()
    }

    private fun findSpringBootVersion(project: Project): String? {
        // build.gradle 또는 pom.xml에서 스프링 부트 버전 추출 로직
        val projectFile = project.projectFile
        val projectBaseDir = projectFile?.parent?.parent

        val buildGradleFile = projectBaseDir?.findChild("build.gradle")

        if (buildGradleFile != null) {
            try {
                val buildGradleContent = String(buildGradleFile.contentsToByteArray())
                println(">>>>> buildGradleContent:\n $buildGradleContent")
                return extractSpringBootVersion(buildGradleContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun retrieveSpringBootDependencies(springBootVersion: String) {
        // Maven Central에서 spring-boot-dependencies POM 파일 다운로드 및 파싱 로직
    }

    companion object {
        private fun extractSpringBootVersion(buildGradleContent: String): String? {
            // val pattern = Pattern.compile("org\\.springframework\\.boot:spring-boot-gradle-plugin:(\\d+\\.\\d+\\.\\d+\\.RELEASE)")
            val pattern = Pattern.compile("id 'org.springframework.boot' version '([\\d.]+)'")
            val matcher = pattern.matcher(buildGradleContent)
            return if (matcher.find()) {
                matcher.group(1)
            } else null
        }
    }
}
