package com.hhplus.dependenciesversionhelper;

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
import java.util.List;

public class DependencyManager {
    private static final List<Dependency> dependencies = new ArrayList<>();

    public static void downloadSpringBootDependenciesPOM(String springBootVersion) {
        String pomUrl = "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/" +
                springBootVersion + "/spring-boot-dependencies-" + springBootVersion + ".pom";
        try {
            URL url = new URL(pomUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            parseDependenciesFromPom(connection.getInputStream());
            connection.disconnect();
        } catch (Exception ex) {
            System.out.println("Failed to download spring-boot-dependencies POM" + ex);
        }
    }

    public static void parseDependenciesFromPom(InputStream pomInputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomInputStream);

            doc.getDocumentElement().normalize();

            NodeList dependencyNodes = doc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Node node = dependencyNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
                    String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
                    String version = element.getElementsByTagName("version").item(0).getTextContent();

                    Dependency dependency = new Dependency(groupId, artifactId, version);
                    dependencies.add(dependency);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Dependency> getDependencies() {
        return dependencies;
    }

    public static void compareWithDependencyManager(List<Dependency> projectDependencies) {
        for (Dependency projectDependency : projectDependencies) {
            boolean isDependencyFound = dependencies.stream()
                    .anyMatch(globalDependency ->
                            globalDependency.getGroupId().equals(projectDependency.getGroupId()) &&
                                    globalDependency.getArtifactId().equals(projectDependency.getArtifactId()));

            if (isDependencyFound) {
                // TODO 찾은 dependencies에 버전이 있으면 popup에 표시 또는 찾는 dependency list만 return
                System.out.println("Dependency found in DependencyManager: " + projectDependency);
            } else {
                System.out.println("Dependency NOT found in DependencyManager: " + projectDependency);
            }
        }
    }
}
