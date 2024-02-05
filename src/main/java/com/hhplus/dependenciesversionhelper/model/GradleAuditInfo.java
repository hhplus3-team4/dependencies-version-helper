package com.hhplus.dependenciesversionhelper.model;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public class GradleAuditInfo {
    private String springBootVersion;
    private List<Dependency> dependencies;
    private VirtualFile gradleFile;

    public GradleAuditInfo(String springBootVersion, List<Dependency> dependencies, VirtualFile gradleFile) {
        this.springBootVersion = springBootVersion;
        this.dependencies = dependencies;
        this.gradleFile = gradleFile;
    }

    public String getSpringBootVersion() {
        return springBootVersion;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public VirtualFile getGradleFile() {
        return gradleFile;
    }
}
