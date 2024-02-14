package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.util.PatternManager;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithGroovy;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithKotlinDsl;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleParserImpl implements GradleParser{

    @Override
    public String findSpringBootVersion(PsiFile psiFile, String gradleFileName) {
        String fileContent = psiFile.getText();

        PatternManager patternManager = createPatternManager(gradleFileName);
        if (patternManager == null) return null;
        Pattern pattern = patternManager.getVersionPattern();

        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @Override
    public List<Dependency> parseGradleDependencies(PsiFile psiFile, String gradleFileName) {
        List<Dependency> dependencies = new ArrayList<>();
        String fileContent = psiFile.getText();

        PatternManager patternManager = createPatternManager(gradleFileName);
        Pattern pattern = patternManager.getDependencyPattern();
        if (pattern == null) return null;

        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String dependencyType = matcher.group(1);
            String groupId = matcher.group(2);
            String artifactId = matcher.group(3);
            String version = matcher.group(4) != null ? matcher.group(4) : "";

            dependencies.add(new Dependency(dependencyType, groupId, artifactId, version));
        }

        return dependencies;
    }

    public PatternManager createPatternManager(String gradleFileName) {
        if(gradleFileName.equals("build.gradle")) {
            return new PatternManagerWithGroovy();
        }

        if(gradleFileName.equals("build.gradle.kts")) {
            return new PatternManagerWithKotlinDsl();
        }

        return null;
    }
}
