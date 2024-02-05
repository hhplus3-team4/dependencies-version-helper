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

        // TODO 현재 파일이 build.gradle 인지 파악해서 그 경우에만 실행되게 할까요...?

        resetDependencyList(); // 리스트 초기화

        String springBootVersion = findSpringBootVersion(project);
        if (springBootVersion != null) {
            System.out.println("springBootVersion=" + springBootVersion);
            downloadSpringBootDependenciesPOM(springBootVersion);
        }

        // 프로젝트에서 dependency 추출
        List<Dependency> dependencies = extractDependenciesFromProject(project);
        System.out.println(dependencies);
        System.out.println();

        // 비교하여 변경해야할 dependency 추출
        List<Dependency> changeDependencies = compareWithDependencyManager(dependencies);

        // 팝업 창에 리스트 띄우기
        SampleDialogWrapper dialog = new SampleDialogWrapper(project, changeDependencies);
        dialog.showAndGet();
    }
}
