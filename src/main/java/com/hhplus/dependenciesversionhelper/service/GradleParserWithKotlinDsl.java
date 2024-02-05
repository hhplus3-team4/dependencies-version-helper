package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.service.GradleParser;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleParserWithKotlinDsl implements GradleParser {
    @Override
    public String findSpringBootVersion(PsiFile psiFile) {
        String fileContent = psiFile.getText();
        Pattern pattern = Pattern.compile("id\\(\"org\\.springframework\\.boot\"\\)\\s+version\\s+\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    @Override
    public List<Dependency> parseGradleDependencies(PsiFile psiFile) {
        List<Dependency> dependencies = new ArrayList<>();

        if (psiFile instanceof KtFile) {
            KtFile ktFile = (KtFile)psiFile;

            String dependencyTypes = String.join("|",
                    "implementation",
                    "testImplementation",
                    "api",
                    "compileOnly",
                    "runtimeOnly",
                    "annotationProcessor",
                    "developmentOnly"
            );
            String groupArtifactVersionPattern = "\\(\"([^:]+):([^:]+):([^\\)\"]+)\"\\)";

            Pattern pattern = Pattern.compile("(" + dependencyTypes + ")\\s+" + groupArtifactVersionPattern);

            PsiTreeUtil.findChildrenOfType(ktFile, KtCallExpression.class).forEach(callExpression -> {
                String text = callExpression.getText();
                Matcher matcher = pattern.matcher(text);

                while (matcher.find()) {
                    String dependencyType = matcher.group(1);
                    String groupId = matcher.group(2);
                    String artifactId = matcher.group(3);
                    String version = matcher.group(4) != null ? matcher.group(4) : "";

                    dependencies.add(new Dependency(dependencyType, groupId, artifactId, version));
                }
            });
        }
        return dependencies;
    }
}
