package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleExtractor {
    // TODO 두 메서드의 중복되는 부분 하나로
    public static String findSpringBootVersion(Project project) {
        try {
            VirtualFile projectBaseDir = ProjectUtil.guessProjectDir(project);

            assert projectBaseDir != null;
            VirtualFile buildGradleFile = projectBaseDir.findChild("build.gradle");

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

    public static List<Dependency> extractDependenciesFromProject(Project project) {
        List<Dependency> dependencies = new ArrayList<>();
        VirtualFile projectBaseDir = ProjectUtil.guessProjectDir(project);
        if (projectBaseDir != null) {
            VirtualFile buildGradleFile = projectBaseDir.findFileByRelativePath("build.gradle");
            if (buildGradleFile != null) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(buildGradleFile.getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    Pattern pattern = Pattern.compile("implementation\\s+'([^:]+):([^:]+)(?::([^']*))?'");

                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String groupId = matcher.group(1);
                            String artifactId = matcher.group(2);
                            String version = matcher.group(3) != null ? matcher.group(3) : "";
                            dependencies.add(new Dependency(groupId, artifactId, version));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return dependencies;
    }
}
