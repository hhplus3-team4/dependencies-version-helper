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
        // Dependency 팝업 띄우기
        SampleDialogWrapper dialog = new SampleDialogWrapper(project);
        dialog.showAndGet();
    }
}
