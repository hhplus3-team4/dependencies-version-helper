package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;

import java.util.List;
import java.util.regex.Pattern;

public class GradleCleanerWithKotlinDsl implements GradleCleaner {

    @Override
    public void cleanDependencyVersion(Project project, VirtualFile gradleFile, List<Dependency> selectedDependencies) {
        Document document = getDocumentFromGradleFile(gradleFile);

        if (document != null) {
            String fileContent = document.getText();
            String modifiedContent = removeDependencyVersions(fileContent, selectedDependencies);

            if (!modifiedContent.equals(fileContent)) {
                updateDocumentContent(project, document, modifiedContent);
                saveDocument(gradleFile);
            }
        }
    }

    private Document getDocumentFromGradleFile(VirtualFile gradleFile) {
        return gradleFile != null ? FileDocumentManager.getInstance().getDocument(gradleFile) : null;
    }

    private String removeDependencyVersions(String fileContent, List<Dependency> selectedDependencies) {
        String modifiedContent = fileContent;

        for (Dependency dependency : selectedDependencies) {
            String dependencyTypes = String.join("|",
                    "implementation",
                    "testImplementation",
                    "api",
                    "compileOnly",
                    "runtimeOnly",
                    "annotationProcessor",
                    "developmentOnly"
            );

            String patternString = "(" + dependencyTypes + ")\\(\\\""
                    + Pattern.quote(dependency.getGroupId())
                    + ":" + Pattern.quote(dependency.getArtifactId()) + ":[^\\\"]+\\\"\\)";

            modifiedContent = modifiedContent.replaceAll(patternString,
                    "$1(\"" + dependency.getGroupId() + ":" + dependency.getArtifactId() + "\")");
        }

        return modifiedContent;
    }

    private void updateDocumentContent(Project project, Document document, String modifiedContent) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.setText(modifiedContent);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }

    private void saveDocument(VirtualFile gradleFile) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(gradleFile);
        if (document != null) {
            fileDocumentManager.saveDocument(document);
        }
    }
}