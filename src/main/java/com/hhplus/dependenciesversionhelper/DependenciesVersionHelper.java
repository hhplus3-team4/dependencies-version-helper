package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependenciesVersionHelper extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println(">>>>>>> actionPerformed");

        Project project = e.getProject();
        if (project == null) return;

        String springBootVersion = findSpringBootVersion(project);
        if (springBootVersion != null) {
            System.out.println("springBootVersion=" + springBootVersion);
            downloadSpringBootDependenciesPOM(springBootVersion);
        }
    }

    private String findSpringBootVersion(Project project) {
        try {
            VirtualFile projectBaseDir = ProjectUtil.guessProjectDir(project);

            System.out.println(projectBaseDir);

            assert projectBaseDir != null;
            VirtualFile buildGradleFile = projectBaseDir.findChild("build.gradle");

            System.out.println(buildGradleFile);

            if (buildGradleFile != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(buildGradleFile.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    Pattern pattern = Pattern.compile("id 'org\\.springframework\\.boot' version '([\\d.]+)'");
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            return matcher.group(1);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void downloadSpringBootDependenciesPOM(String springBootVersion) {
        String pomUrl = "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/" +
                springBootVersion + "/spring-boot-dependencies-" + springBootVersion + ".pom";
        try {
            URL url = new URL(pomUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            // inputStream에서 POM 파일을 읽고 처리하는 로직을 구현
            connection.disconnect();
        } catch (Exception ex) {
            System.out.println("Failed to download spring-boot-dependencies POM" + ex);
        }
    }
}
