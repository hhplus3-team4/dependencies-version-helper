package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyComparator {

    public List<Dependency> compareWithDependencyManager(List<Dependency> projectDependencies, List<Dependency> pomDependencies) {
        List<Dependency> changeDependencies = new ArrayList<>();
        for (Dependency projectDependency : projectDependencies) {
            boolean isDependencyFound = pomDependencies.stream()
                    .anyMatch(globalDependency ->
                            globalDependency.getGroupId().equals(projectDependency.getGroupId()) &&
                                    globalDependency.getArtifactId().equals(projectDependency.getArtifactId()));


            if (isDependencyFound) {
                // 찾은 dependencies에 버전이 있으면 변경할 dependency list return
                if (!projectDependency.getVersion().equals("")) {
                    Dependency changeDependency = new Dependency(projectDependency.getDependencyType(), projectDependency.getGroupId(), projectDependency.getArtifactId(), projectDependency.getVersion());
                    changeDependencies.add(changeDependency);
                }
            }
        }

        return changeDependencies;
    }

}
