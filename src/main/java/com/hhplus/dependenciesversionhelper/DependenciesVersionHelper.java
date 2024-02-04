package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.List;

import static com.hhplus.dependenciesversionhelper.DependencyManager.*;
import static com.hhplus.dependenciesversionhelper.GradleExtractor.*;

public class DependenciesVersionHelper extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        String springBootVersion = findSpringBootVersion(project);
        if (springBootVersion != null) {
            System.out.println("springBootVersion=" + springBootVersion);
            downloadSpringBootDependenciesPOM(springBootVersion);
        }

        List<Dependency> dependencies = extractDependenciesFromProject(project);
        System.out.println(dependencies);
        System.out.println();

        compareWithDependencyManager(dependencies);
    }
}
