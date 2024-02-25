package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public interface GradleCleaner {

    public void removeVersion(Project project, VirtualFile gradleFile, List<Dependency> selectedDependencies);

    public void addVersion(Project project, VirtualFile gradleFile, List<Dependency> versionlessDependencies);
}
