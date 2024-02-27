package com.hhplus.dependenciesversionhelper;

import com.hhplus.dependenciesversionhelper.ui.DependencyCleanerDialogWrapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class DependenciesVersionHelper extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // Dependency 팝업 띄우기
        DependencyCleanerDialogWrapper dialog = new DependencyCleanerDialogWrapper(project);
        dialog.showAndGet();
    }
}
