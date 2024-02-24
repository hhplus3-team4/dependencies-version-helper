package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.util.DocumentManager;
import com.hhplus.dependenciesversionhelper.util.PatternManager;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithGroovy;
import com.hhplus.dependenciesversionhelper.util.PatternManagerWithKotlinDsl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public class GradleCleanerImpl implements GradleCleaner {

    @Override
    public void removeVersion(Project project, VirtualFile gradleFile, List<Dependency> selectedDependencies) {
        Document document = FileDocumentManager.getInstance().getDocument(gradleFile);
        PatternManager patternManager = createPatternManager(gradleFile.getName());
        if (patternManager == null) return;

        if (document != null) {
            String fileContent = document.getText();
            String modifiedContent = fileContent;

            for(Dependency dependency : selectedDependencies){
                String pattern = patternManager.getRemoveDependenciesMatchPattern(dependency.getGroupId(), dependency.getArtifactId());
                modifiedContent = modifiedContent.replaceAll(pattern,
                        patternManager.getDependencyRemovalReplacementPattern(dependency.getGroupId(), dependency.getArtifactId()));
            }

            if (!modifiedContent.equals(fileContent)) {
                DocumentManager documentManager = new DocumentManager();

                documentManager.updateContent(project, document, modifiedContent);
                documentManager.saveContent(gradleFile);
            }
        }
    }

    @Override
    public void addNeedVersion(Project project, VirtualFile gradleFile, List<Dependency> versionlessDependencies) {
        Document document = FileDocumentManager.getInstance().getDocument(gradleFile);
        PatternManager patternManager = createPatternManager(gradleFile.getName());
        if (patternManager == null) return;

        if (document != null) {
            String fileContent = document.getText();
            String modifiedContent = fileContent;

            for(Dependency dependency : versionlessDependencies){
                if (!dependency.getVersion().equals("Need_Version")) {
                    String pattern = patternManager.getAddVersionDependenciesMatchPattern(dependency.getGroupId(), dependency.getArtifactId());
                    modifiedContent = modifiedContent.replaceAll(pattern,
                            patternManager.getDependencyAddVersionReplacementPattern(dependency.getGroupId(), dependency.getArtifactId()));
                }
            }

            if (!modifiedContent.equals(fileContent)) {
                DocumentManager documentManager = new DocumentManager();

                documentManager.updateContent(project, document, modifiedContent);
                documentManager.saveContent(gradleFile);
            }
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