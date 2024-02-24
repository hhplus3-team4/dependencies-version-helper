package com.hhplus.dependenciesversionhelper.model;

import java.util.List;

public class DependencyAnalyzer {
    private List<Dependency> versionedManagedDependencies;
    private List<Dependency> versionlessUnmanagedDependencies;

    public DependencyAnalyzer(List<Dependency> versionedManagedDependencies, List<Dependency> versionlessUnmanagedDependencies) {
        this.versionedManagedDependencies = versionedManagedDependencies;
        this.versionlessUnmanagedDependencies = versionlessUnmanagedDependencies;
    }

    public List<Dependency> getVersionedManagedDependencies() {
        return versionedManagedDependencies;
    }

    public List<Dependency> getVersionlessUnmanagedDependencies() {
        return versionlessUnmanagedDependencies;
    }
}
