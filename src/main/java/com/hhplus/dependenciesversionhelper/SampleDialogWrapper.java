package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hhplus.dependenciesversionhelper.GradleFileEditor.removeDependencyVersion;

public class SampleDialogWrapper extends DialogWrapper {
    private final Project project;
    private final List<Dependency> dependencies;
    private final Map<JCheckBox, Dependency> checkBoxDependencyMap = new HashMap<>();

    public SampleDialogWrapper(Project project, List<Dependency> dependencies) {
        super(project,true); // use current window as parent
        this.project = project;
        this.dependencies = dependencies;
        // 창 타이틀
        setTitle("Test DialogWrapper");
        init();
    }

    @Override
    protected Action @NotNull [] createActions() { // 버튼 생성
        OkAction okAction = new OkAction("OK");
        CancelAction cancelAction = new CancelAction("CANCEL");
        return new Action[] { okAction, cancelAction };
    }

    @Override
    protected JComponent createCenterPanel() { // 패널 내용 채우된
        // 창 사이즈
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setPreferredSize(new Dimension(500, 500));

        for (Dependency dependency : dependencies) {
            JCheckBox checkBox = new JCheckBox(dependency.deserialize());
            checkBoxDependencyMap.put(checkBox, dependency);
            dialogPanel.add(checkBox);
        }

        return dialogPanel;
    }

    protected final class OkAction extends DialogWrapperAction {
        private OkAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (project != null) {
                // 체크된 의존성만 추출
                List<Dependency> selectedDependencies = checkBoxDependencyMap.entrySet().stream()
                        .filter(entry -> entry.getKey().isSelected())
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

                // 선택된 의존성의 버전 제거
                System.out.println(">>> selected => " + selectedDependencies);
                removeDependencyVersion(project, selectedDependencies);
            }
            close(OK_EXIT_CODE);
        }
    }

    protected final class CancelAction extends DialogWrapperAction {
        private CancelAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            dispose();
        }
    }
}