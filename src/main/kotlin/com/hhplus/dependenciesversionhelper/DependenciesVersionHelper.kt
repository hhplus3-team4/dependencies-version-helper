package com.hhplus.dependenciesversionhelper

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.net.URL
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory


class DependenciesVersionHelper : ProjectManagerListener {
    /**
     * 전역 변수로 dependencies 리스트 선언
     * TODO: 다른 방식의 저장 고민 (상태 관리 등)
     */
    private var managedDependencies = mutableListOf<Dependency>()

    private val connection: MessageBusConnection

    init {
        connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(ProjectManager.TOPIC, this)
    }

    /**
     * 프로젝트 오픈 시 수행
     * TODO: 실행 주기 등으로 추가로 고민 (Action 도 고민)
     */
    override fun projectOpened(project: Project) {
        // TODO 1. SpringBoot Managed Dependencies 조회
        val springBootVersion = findSpringBootVersion(project)
        println(">>>>> SpringBootVersion: $springBootVersion")
        springBootVersion?.let { downloadSpringBootDependenciesPom(it) }

        // TODO 2. 오픈된 프로젝트의 build.gradle 파일의 dependencies 조회

        // TODO 3. 오픈된 프로젝트에서 사용되는 dependencies가 관리되고 있는 것인지 비교

        // TODO 4. 관리되고 있는 경우 warning 표시
    }

    fun dispose() {
        connection.disconnect()
    }

    /**
     * 오픈된 프로젝트에서 build.gradle 파일 내 springboot version 확인
     * TODO: 프로젝트 내 build.gradle 파일을 파싱하는 부분은 추후에도 사용할 것 같아서 별도 메서드로 분리
     */
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

    private fun extractSpringBootVersion(buildGradleContent: String): String? {
        // val pattern = Pattern.compile("org\\.springframework\\.boot:spring-boot-gradle-plugin:(\\d+\\.\\d+\\.\\d+\\.RELEASE)")
        val pattern = Pattern.compile("id 'org.springframework.boot' version '([\\d.]+)'")
        val matcher = pattern.matcher(buildGradleContent)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }

    /**
     * spring-boot-dependencies pom 파일 조회 후 Dependency 클래스 리스트로 변환 후 저장
     * TODO: 실행 주기 확인 및 저장 방식 고민
     */
    private fun downloadSpringBootDependenciesPom(springBootVersion: String) {
        val pomUrl = "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/" +
                "$springBootVersion/spring-boot-dependencies-$springBootVersion.pom"

        try {
            URL(pomUrl).openStream().use { inputStream ->
                val dependencies = parseDependenciesFromPom(inputStream)
                dependencies.forEach { println(it) }
                managedDependencies.addAll(dependencies)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseDependenciesFromPom(inputStream: InputStream): List<Dependency> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val input = builder.parse(inputStream)

        val dependencies = mutableListOf<Dependency>()
        val nodes = input.getElementsByTagName("dependency")

        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val groupId = element.getElementsByTagName("groupId").item(0).textContent
                val artifactId = element.getElementsByTagName("artifactId").item(0).textContent
                dependencies.add(Dependency(groupId, artifactId))
            }
        }

        return dependencies
    }
}
