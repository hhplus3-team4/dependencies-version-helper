package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.util.PatternManager;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithGroovy;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithKotlinDsl;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;

import java.util.List;

public class GradleCleanerImpl implements GradleCleaner {

    @Override
    public void cleanDependencyVersion(Project project, VirtualFile gradleFile, List<Dependency> selectedDependencies) {
        Document document = FileDocumentManager.getInstance().getDocument(gradleFile);
        PatternManager patternManager = createPatternManager(gradleFile.getName());
        if (patternManager == null) return;

        if (document != null) {
            String fileContent = document.getText();
            String modifiedContent = fileContent;

            for(Dependency dependency : selectedDependencies){
                String pattern = patternManager.getDependencyCleanPattern(dependency.getGroupId(), dependency.getArtifactId());
                modifiedContent = modifiedContent.replaceAll(pattern,
                        patternManager.getDependencyReplacementPattern(dependency.getGroupId(), dependency.getArtifactId()));
            }

            if (!modifiedContent.equals(fileContent)) {
                updateDocumentContent(project, document, modifiedContent);
                saveDocument(gradleFile);
            }
        }
    }

    public void updateDocumentContent(Project project, Document document, String modifiedContent) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.setText(modifiedContent);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }

    public void saveDocument(VirtualFile gradleFile) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(gradleFile);
        if (document != null) {
            fileDocumentManager.saveDocument(document);
        }
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