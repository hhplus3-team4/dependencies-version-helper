package com.hhplus.dependenciesversionhelper.util;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependenciesFetcher {

    public List<Dependency> fetchSpringBootDependenciesPOM(String springBootVersion) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            // URL에서 POM 파일 스트림을 직접 얻어옵니다.
            URL url = new URL("https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/" + springBootVersion + "/spring-boot-dependencies-" + springBootVersion + ".pom");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream pomInputStream = connection.getInputStream()) {
                // POM 파일 스트림을 파싱하여 의존성 리스트를 얻어옵니다.
                dependencies = parseDependenciesFromPom(pomInputStream);
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("DependenciesVersionHelper", "Spring Boot Dependencies fetch Error",
                    "Error occurred while reading Spring Boot dependency POM file: " + e.getMessage(), NotificationType.ERROR));
        }

        return dependencies;
    }

    private List<Dependency> parseDependenciesFromPom(InputStream pomInputStream) {
        List<Dependency> dependencies = new ArrayList<>(); // list reset
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

                    Dependency dependency = new Dependency("", groupId, artifactId, version);
                    dependencies.add(dependency);
                }
            }

            return dependencies;
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("DependenciesVersionHelper", "Spring Boot Dependencies fetch Error",
                    "Error occurred while reading Spring Boot dependency POM file: " + e.getMessage(), NotificationType.ERROR));
        }
        return new ArrayList<>();
    }
}
