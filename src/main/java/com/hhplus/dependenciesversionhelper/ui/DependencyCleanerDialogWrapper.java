package com.hhplus.dependenciesversionhelper.ui;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.model.GradleAuditInfo;
import com.hhplus.dependenciesversionhelper.service.*;
import com.hhplus.dependenciesversionhelper.util.DependenciesFetcher;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhplus.dependenciesversionhelper.util.GradleScanner.scanForGradleBuildFiles;

public class DependencyCleanerDialogWrapper extends DialogWrapper {
    private final Project project;
    private List<GradleAuditInfo> gradleAuditInfos;
    private DefaultTableModel tableModel;
    private Map<GradleAuditInfo, DefaultTableModel> infoToTableModelMap = new HashMap<>();
    private JBTabbedPane tabbedPane = new JBTabbedPane();



    public DependencyCleanerDialogWrapper(Project project) {
        super(project,true); // use current window as parent
        this.project = project;
        this.gradleAuditInfos = auditDependenciesAcrossProject(project);
        setTitle("Dependencies Version Helper");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        UIComponentManager uiComponentManager = new UIComponentManager();

        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel descriptionLabel = uiComponentManager.createLabel();
        // 디펜던시 로드

        // 테이블을 스크롤 패널에 추가
        for(GradleAuditInfo gradleAuditInfo : gradleAuditInfos){
            String projectBasePath = project.getBasePath();
            String absolutePath = gradleAuditInfo.getGradleFile().getPath();
            String relativePath = absolutePath.substring(projectBasePath.length());

            tableModel = uiComponentManager.createTableModel(relativePath, gradleAuditInfo.getSpringBootVersion());

            infoToTableModelMap.put(gradleAuditInfo, tableModel);

            JTable table = uiComponentManager.createTable(tableModel, gradleAuditInfo.getDependencies());
            JScrollPane tableScrollPane = new JBScrollPane(table);

            tabbedPane.addTab(relativePath, tableScrollPane);
        }

        // 전체 패널에 라벨과 스크롤 패널 추가
        dialogPanel.add(tabbedPane, BorderLayout.CENTER);
        dialogPanel.add(descriptionLabel, BorderLayout.NORTH);

        dialogPanel.setPreferredSize(new Dimension(500, 500));

        return dialogPanel;
    }

    private List<GradleAuditInfo> auditDependenciesAcrossProject(Project project) {
        List<VirtualFile> gradleFiles = scanForGradleBuildFiles(project);
        List<GradleAuditInfo> gradleAuditInfos = new ArrayList<>();

        for(VirtualFile gradleFile : gradleFiles) {
            GradleAuditInfo gradleAuditInfo = auditDependenciesForGradleFile(gradleFile);
            gradleAuditInfos.add(gradleAuditInfo);
        }

        return gradleAuditInfos;
    }

    private GradleAuditInfo auditDependenciesForGradleFile(VirtualFile gradleFile) {
        GradleParser gradleParser = new GradleParserImpl();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(gradleFile);
        DependenciesFetcher dependenciesFetcher = new DependenciesFetcher();
        DependencyComparator dependencyComparator = new DependencyComparator();

        String springBootVersion = gradleParser.findSpringBootVersion(psiFile, gradleFile.getName());
        // 만약 SpringBoot 버전을 읽는 데에 실패한다면, 빈 배열을 반환한다.
        if (springBootVersion == null) return new GradleAuditInfo("",
                new ArrayList<>(),
                gradleFile);

        List<Dependency> pomDependencies = dependenciesFetcher.fetchSpringBootDependenciesPOM(springBootVersion);
        List<Dependency> projectDependencies = gradleParser.parseGradleDependencies(psiFile, gradleFile.getName());

        return new GradleAuditInfo(springBootVersion,
                dependencyComparator.compareWithDependencyManager(projectDependencies, pomDependencies),
                gradleFile);
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
            gradleAuditInfos.forEach(this::processCleanGradleFile);
            close(OK_EXIT_CODE);
        }

        private void processCleanGradleFile(GradleAuditInfo gradleAuditInfo) {
            VirtualFile gradleFile = gradleAuditInfo.getGradleFile();
            DefaultTableModel tableModel = infoToTableModelMap.get(gradleAuditInfo);

            List<Dependency> selectedDependencies = getSelectedDependencies(gradleAuditInfo, tableModel);

            cleanDependencies(gradleFile, selectedDependencies);
        }

        private List<Dependency> getSelectedDependencies(GradleAuditInfo gradleAuditInfo, DefaultTableModel tableModel) {
            List<Dependency> selectedDependencies = new ArrayList<>();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);

                if (Boolean.TRUE.equals(isSelected)) {
                    Dependency selectedDependency = gradleAuditInfo.getDependencies().get(i);
                    selectedDependencies.add(selectedDependency);
                }
            }

            return selectedDependencies;
        }

        private void cleanDependencies(VirtualFile gradleFile, List<Dependency> selectedDependencies) {
            GradleCleaner gradleCleaner = new GradleCleanerImpl();
            gradleCleaner.cleanDependencyVersion(project, gradleFile, selectedDependencies);
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