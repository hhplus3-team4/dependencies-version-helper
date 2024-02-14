package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
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
            System.out.println("kotlin" + matcher.group(1));

            return matcher.group(1);
        }

        return null;
    }

    @Override
    public List<Dependency> parseGradleDependencies(PsiFile psiFile) {
        List<Dependency> dependencies = new ArrayList<>();
        String fileContent = psiFile.getText();

        String dependencyTypes = String.join("|",
                "implementation",
                "testImplementation",
                "compileOnly",
                "runtimeOnly",
                "testRuntimeOnly",
                "testCompileOnly",
                "api",
                "annotationProcessor",
                "developmentOnly"
        );
        String groupArtifactVersionPattern = "['\"]([^':]+):([^':]+):([^'\"\\s]+)['\"]";

        Pattern pattern = Pattern.compile("(" + dependencyTypes + ")\\s*\\(" + groupArtifactVersionPattern + "\\)");
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String dependencyType = matcher.group(1);
            String groupId = matcher.group(2);
            String artifactId = matcher.group(3);
            String version = matcher.group(4);

            dependencies.add(new Dependency(dependencyType, groupId, artifactId, version));
        }

        return dependencies;
    }
}
