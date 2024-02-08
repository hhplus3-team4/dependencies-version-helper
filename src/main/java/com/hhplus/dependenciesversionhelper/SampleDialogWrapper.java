package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.hhplus.dependenciesversionhelper.DependencyManager.downloadSpringBootDependenciesPOM;
import static com.hhplus.dependenciesversionhelper.GradleExtractor.findSpringBootVersion;
import static com.hhplus.dependenciesversionhelper.GradleFileEditor.removeDependencyVersion;
import static com.hhplus.dependenciesversionhelper.UIComponentManager.*;

public class SampleDialogWrapper extends DialogWrapper {
    private final Project project;
    private List<Dependency> dependencies;

    private DefaultTableModel tableModel;

    public SampleDialogWrapper(Project project) {
        super(project,true); // use current window as parent
        this.project = project;
        dependencies = loadChangeableDependencies(project);
        setTitle("Dependencies Version Helper");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JLabel descriptionLabel = createLabel();
        // 디펜던시 로드

        // 테이블을 스크롤 패널에 추가
        tableModel = createTableModel();
        JTable table = createTable(tableModel, dependencies);
        JScrollPane tableScrollPane = new JBScrollPane(table);

        // 전체 패널에 라벨과 스크롤 패널 추가
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(500, 500));
        dialogPanel.add(descriptionLabel, BorderLayout.NORTH);
        dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

        return dialogPanel;
    }

    private List<Dependency> loadChangeableDependencies(Project project) {
        String springBootVersion = findSpringBootVersion(project);
        // 만약 SpringBoot 버전을 읽는 데에 실패한다면, 빈 배열을 반환한다.
        if (springBootVersion == null) return new ArrayList<>();
        List<Dependency> pomDependencies = downloadSpringBootDependenciesPOM(springBootVersion);
        List<Dependency> projectDependencies = GradleExtractor.extractDependenciesFromProject(project);
        return DependencyManager.compareWithDependencyManager(projectDependencies, pomDependencies);
    }

    @Override
    protected Action @NotNull [] createActions() { // 버튼 생성
        OkAction okAction = new OkAction("OK");
        CancelAction cancelAction = new CancelAction("CANCEL");
        return new Action[] { okAction, cancelAction };
    }

    protected final class OkAction extends DialogWrapperAction {
        private OkAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (project != null) {
                List<Dependency> selectedDependencies = new ArrayList<>();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                    if (isSelected) {
                        Dependency selectedDependency = dependencies.get(i); // 리스트에서 Dependency 객체 가져오기
                        selectedDependencies.add(selectedDependency);
                    }
                }

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