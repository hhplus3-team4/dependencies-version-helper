package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.service.GradleParser;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleParserWithGroovy implements GradleParser {
    @Override
    public String findSpringBootVersion(PsiFile psiFile) {
        String fileContent = psiFile.getText();
        Pattern pattern = Pattern.compile("id 'org\\.springframework\\.boot' version '([^']+)'");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }


    @Override
    public List<Dependency> parseGradleDependencies(PsiFile psiFile) {
        List<Dependency> dependencies = new ArrayList<>();

        if (psiFile instanceof GroovyFile) {
            GroovyFile groovyFile = (GroovyFile)psiFile;

            String dependencyTypes = String.join("|",
                    "implementation",
                    "testImplementation",
                    "api",
                    "compileOnly",
                    "runtimeOnly",
                    "annotationProcessor",
                    "developmentOnly"
            );

            String groupArtifactVersionPattern = "['\"]([^:']+)[:]?([^:']*?)(?::([^']+))?['\"]";

            Pattern pattern = Pattern.compile("(" + dependencyTypes + ")\\s+" + groupArtifactVersionPattern, Pattern.DOTALL);

            for (GrMethodCall methodCall : PsiTreeUtil.findChildrenOfType(groovyFile, GrMethodCall.class)) {
                String text = methodCall.getText();

                if (!text.startsWith("dependencies {") && text.matches(".*(" + dependencyTypes + ").*")) {
                    Matcher matcher = pattern.matcher(text);

                    while (matcher.find()) {
                        String dependencyType = matcher.group(1);
                        String groupId = matcher.group(2);
                        String artifactId = matcher.group(3);
                        String version = matcher.group(4) != null ? matcher.group(4) : "";

                        dependencies.add(new Dependency(dependencyType, groupId, artifactId, version));
                    }
                }
            }
        }
        return dependencies;
    }
}
