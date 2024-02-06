package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.application.PathManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyManager {
    private static String springBootVersion;
    private static List<Dependency> dependencies = new ArrayList<>();
    private static List<Dependency> changeDependencies = new ArrayList<>();

    public static String getSpringBootVersion() {
        return springBootVersion;
    }

    public static void downloadSpringBootDependenciesPOM(String version) {
        springBootVersion = version;

        Path cachePath = Paths.get(PathManager.getPluginTempPath(), "spring-boot-dependencies");
        System.out.println("POM 파일 저장 경로: " + cachePath);

        Path pomFile = cachePath.resolve("spring-boot-dependencies-" + springBootVersion + ".pom.xml");

        try {
            if (!Files.exists(pomFile)) {
                Files.createDirectories(cachePath); // 디렉토리가 없으면 생성

                // 파일이 캐시에 없으면 다운로드
                URL url = new URL("https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/" +
                        springBootVersion + "/spring-boot-dependencies-" + springBootVersion + ".pom");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // 스트림을 통해 파일에 쓰기
                Files.copy(connection.getInputStream(), pomFile);
                connection.disconnect();
                System.out.println("POM 파일 다운로드 및 저장: " + pomFile);
            }

            // 파일이 이미 존재하거나 다운로드 후, 해당 파일을 파싱
            try (InputStream pomInputStream = Files.newInputStream(pomFile)) {
                parseDependenciesFromPom(pomInputStream);
            }
        } catch (Exception ex) {
            System.out.println("POM 파일 처리 중 오류 발생: " + ex.getMessage());
        }
    }

    public static void parseDependenciesFromPom(InputStream pomInputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomInputStream);

            doc.getDocumentElement().normalize();

            // <properties> 태그 내의 모든 속성과 값을 사전에 저장
            Element propertiesElement = (Element) doc.getElementsByTagName("properties").item(0);
            NodeList properties = propertiesElement.getChildNodes();
            Map<String, String> propertyValues = new HashMap<>();
            for (int i = 0; i < properties.getLength(); i++) {
                if (properties.item(i) instanceof Element property) {
                    propertyValues.put(property.getTagName(), property.getTextContent());
                }
            }

            NodeList dependencyNodes = doc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Node node = dependencyNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
                    String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
                    String version = element.getElementsByTagName("version").item(0).getTextContent();

                    // 버전 플레이스홀더 대체
                    String versionPattern = "\\$\\{(.+?)\\}";
                    Matcher matcher = Pattern.compile(versionPattern).matcher(version);
                    if (matcher.find()) {
                        String propertyName = matcher.group(1);
                        version = propertyValues.getOrDefault(propertyName, "unknown");
                    }

                    Dependency dependency = new Dependency(groupId, artifactId, version);
                    dependencies.add(dependency);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetDependencyList() { // 다시 누를때마다 초기화
        dependencies = new ArrayList<>();
        changeDependencies = new ArrayList<>();
    }

    public static List<Dependency> compareWithDependencyManager(List<Dependency> projectDependencies) {
        for (Dependency projectDependency : projectDependencies) {
            boolean isDependencyFound = dependencies.stream()
                    .anyMatch(globalDependency ->
                            globalDependency.getGroupId().equals(projectDependency.getGroupId()) &&
                                    globalDependency.getArtifactId().equals(projectDependency.getArtifactId()));

            if (isDependencyFound) {
                System.out.println("Dependency found in DependencyManager: " + projectDependency);

                // 찾은 dependencies에 버전이 있으면 변경할 dependency list return
                if (!projectDependency.getVersion().equals("")) {
                    Dependency changeDependency = new Dependency(projectDependency.getGroupId(), projectDependency.getArtifactId(), projectDependency.getVersion());
                    changeDependencies.add(changeDependency);
                }
            } else {
                System.out.println("Dependency NOT found in DependencyManager: " + projectDependency);
            }
        }

        return changeDependencies;
    }

}
