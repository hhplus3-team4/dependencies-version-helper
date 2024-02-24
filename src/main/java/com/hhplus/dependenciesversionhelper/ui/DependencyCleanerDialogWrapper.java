package com.hhplus.dependenciesversionhelper.ui;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.model.DependencyAnalyzer;
import com.hhplus.dependenciesversionhelper.model.GradleDependencyAnalysis;
import com.hhplus.dependenciesversionhelper.service.*;
import com.hhplus.dependenciesversionhelper.util.DependenciesFetcher;
import com.hhplus.dependenciesversionhelper.util.SpringBootVersionFinder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
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
    private String springBootVersion;
    private List<Dependency> pomDependencies;
    private List<GradleDependencyAnalysis> gradleDependencyAnalyses;
    private Map<GradleDependencyAnalysis, DefaultTableModel> infoToTableModelMap = new HashMap<>();
    private JBTabbedPane tabbedPane;



    public DependencyCleanerDialogWrapper(Project project) {
        super(project,true); // use current window as parent
        SpringBootVersionFinder springBootVersionFinder = new SpringBootVersionFinder();
        DependenciesFetcher dependenciesFetcher = new DependenciesFetcher();

        this.project = project;
        this.springBootVersion = springBootVersionFinder.find(project);
        this.pomDependencies = dependenciesFetcher.fetchSpringBootDependenciesPOM(springBootVersion);
        this.gradleDependencyAnalyses = auditDependenciesAcrossProject(project);

        setTitle("Dependencies Version Helper");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        UIComponentManager uiComponentManager = new UIComponentManager();

        JPanel dialogPanel = new JPanel(new BorderLayout());
        tabbedPane = new JBTabbedPane();
        JLabel descriptionLabel = uiComponentManager.createLabel(springBootVersion);

        // 테이블을 스크롤 패널에 추가
        for(GradleDependencyAnalysis analysis : gradleDependencyAnalyses){
            // 두 테이블을 수용할 패널 생성
            JPanel tablesPanel = new JPanel();
            tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));

            String projectBasePath = project.getBasePath();
            String absolutePath = analysis.getGradleFile().getPath();
            String relativePath = absolutePath.substring(projectBasePath.length());

            DefaultTableModel versionedModel = uiComponentManager.createVersionedTableModel();
            JTable versionedTable = uiComponentManager.createVersionedTable(versionedModel, analysis.getDependencyAnalyzer().getVersionedManagedDependencies());
            tablesPanel.add(new JScrollPane(versionedTable));

            infoToTableModelMap.put(analysis, versionedModel);

            DefaultTableModel versionlessModel = uiComponentManager.createVersionlessTableModel();
            JTable versionlessTable = uiComponentManager.createVersionlessTable(versionlessModel, analysis.getDependencyAnalyzer().getVersionlessUnmanagedDependencies());
            tablesPanel.add(new JScrollPane(versionlessTable));

            tabbedPane.addTab(relativePath, tablesPanel);
        }

        // 전체 패널에 라벨과 스크롤 패널 추가
        dialogPanel.add(tabbedPane, BorderLayout.CENTER);
        dialogPanel.add(descriptionLabel, BorderLayout.NORTH);

        dialogPanel.setPreferredSize(new Dimension(600, 600));

        return dialogPanel;
    }

    private List<GradleDependencyAnalysis> auditDependenciesAcrossProject(Project project) {
        List<VirtualFile> gradleFiles = scanForGradleBuildFiles(project);
        List<GradleDependencyAnalysis> gradleDependencyAnalyses = new ArrayList<>();

        for(VirtualFile gradleFile : gradleFiles) {
            GradleDependencyAnalysis gradleDependencyAnalysis = auditDependenciesForGradleFile(gradleFile);
            gradleDependencyAnalyses.add(gradleDependencyAnalysis);
        }

        return gradleDependencyAnalyses;
    }

    private GradleDependencyAnalysis auditDependenciesForGradleFile(VirtualFile gradleFile) {
        GradleParser gradleParser = new GradleParserImpl();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(gradleFile);
        DependencyComparator dependencyComparator = new DependencyComparator();

        // 만약 SpringBoot 버전을 읽는 데에 실패한다면, 빈 배열을 반환한다.
        if (springBootVersion == null) return new GradleDependencyAnalysis(
                new DependencyAnalyzer(new ArrayList<>(), new ArrayList<>()),
                gradleFile);

        List<Dependency> projectDependencies = gradleParser.parseGradleDependencies(psiFile, gradleFile.getName());

        return new GradleDependencyAnalysis(
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
            gradleDependencyAnalyses.forEach(analysis -> {
                VirtualFile gradleFile = analysis.getGradleFile();
                DefaultTableModel versionedModel = infoToTableModelMap.get(analysis);

                // versionedTable에서 선택된 의존성 처리
                List<Dependency> selectedDependencies = getSelectedDependencies(analysis, versionedModel);

                if (selectedDependencies.size() > 0) {
                    GradleCleaner gradleCleaner = new GradleCleanerImpl();
                    gradleCleaner.removeVersion(project, gradleFile, selectedDependencies);
                }

                // versionlessTable의 모든 의존성에 "Need_Version" 추가
                List<Dependency> versionlessUnmanagedDependencies = analysis.getDependencyAnalyzer().getVersionlessUnmanagedDependencies();

                if(versionlessUnmanagedDependencies.size() > 0) {
                    GradleCleaner gradleCleaner = new GradleCleanerImpl();
                    gradleCleaner.addNeedVersion(project, gradleFile, versionlessUnmanagedDependencies);
                }
            });
            close(OK_EXIT_CODE);
        }

        private List<Dependency> getSelectedDependencies(GradleDependencyAnalysis gradleDependencyAnalysis, DefaultTableModel tableModel) {
            List<Dependency> selectedDependencies = new ArrayList<>();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);

                if (Boolean.TRUE.equals(isSelected)) {
                    Dependency selectedDependency = gradleDependencyAnalysis.getDependencyAnalyzer().getVersionedManagedDependencies().get(i);
                    selectedDependencies.add(selectedDependency);
                }
            }

            return selectedDependencies;
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