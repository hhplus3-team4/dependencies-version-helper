package com.hhplus.dependenciesversionhelper.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;

import java.util.ArrayList;
import java.util.List;

public class GradleScanner {

    public static List<VirtualFile> scanForGradleBuildFiles(Project project) {
        List<VirtualFile> gradleFiles = new ArrayList<>();
        ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
        VirtualFile baseDir = project.getBaseDir();

        VfsUtil.visitChildrenRecursively(baseDir, new VirtualFileVisitor<Void>() {
            @Override
            public boolean visitFile(VirtualFile file) {
                if (projectFileIndex.isInContent(file)) {
                    String fileName = file.getName();
                    if ("build.gradle".equals(fileName) || "build.gradle.kts".equals(fileName)) {
                        gradleFiles.add(file);
                    }
                }
                return true;
            }
        });

        return gradleFiles;
    }
}
