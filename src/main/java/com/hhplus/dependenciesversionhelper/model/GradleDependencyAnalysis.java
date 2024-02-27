package com.hhplus.dependenciesversionhelper.model;

import com.intellij.openapi.vfs.VirtualFile;

public class GradleDependencyAnalysis {
    private DependencyAnalyzer dependencyAnalyzer;
    private VirtualFile gradleFile;

    public GradleDependencyAnalysis(DependencyAnalyzer dependencyAnalyzer, VirtualFile gradleFile) {
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.gradleFile = gradleFile;
    }

    public DependencyAnalyzer getDependencyAnalyzer() {
        return dependencyAnalyzer;
    }

    public VirtualFile getGradleFile() {
        return gradleFile;
    }
}
