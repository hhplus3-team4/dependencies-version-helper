package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
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
        setTitle("Dependencies Version Helper");
        init();
    }

    @Override
    protected Action @NotNull [] createActions() { // 버튼 생성
        OkAction okAction = new OkAction("OK");
        CancelAction cancelAction = new CancelAction("CANCEL");
        return new Action[] { okAction, cancelAction };
    }

    @Override
    protected JComponent createCenterPanel() {
        String springBootVersion = DependencyManager.getSpringBootVersion();

        // 라벨 여백 조정
        JLabel descriptionLabel = new JLabel("<html><b>SpringBoot Version " + springBootVersion + " Managed Dependencies.</b><br><br>"
                + "The following dependencies are managed by SpringBoot, so specifying a version is unnecessary.<br>"
                + "If you wish for versions to be automatically managed, please select the checkboxes.<br></html>");
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // 상하 여백은 유지하고 좌우 여백을 제거합니다.

        // 체크박스 패널 배경색 변경
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        // 체크박스 추가
        for (Dependency dependency : dependencies) {
            JCheckBox checkBox = new JCheckBox(dependency.deserialize());
            checkBoxDependencyMap.put(checkBox, dependency);
            checkBoxPanel.add(checkBox);
        }

        // 체크박스 패널을 스크롤 패널에 추가하고, 스크롤 패널의 배경색도 변경합니다.
        JScrollPane scrollPane = new JBScrollPane(checkBoxPanel);
        scrollPane.getViewport().setBackground(JBColor.DARK_GRAY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        /**
         * TODO 전체 선택 있으면 좋을 것 같아서 테이블 형태로 바꿔보면 어떨까요??
        // 테이블 모델 생성
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Select", "Dependencies"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 첫 번째 열(체크박스)만 편집 가능
            }
        };

        // 의존성 데이터를 테이블 모델에 추가
        for (Dependency dependency : dependencies) {
            tableModel.addRow(new Object[]{false, dependency.deserialize()});
        }

        // 테이블 생성
        JTable table = new JBTable(tableModel);

        // 체크박스 렌더러 및 에디터 설정
        TableColumn selectColumn = table.getColumnModel().getColumn(0);
        selectColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        selectColumn.setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> new JCheckBox("", (Boolean) value));

        // 헤더 체크박스 설정
        JCheckBox selectAllCheckBox = new JCheckBox();
        selectAllCheckBox.addActionListener(e -> {
            boolean isChecked = selectAllCheckBox.isSelected();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(isChecked, i, 0);
            }
        });
        selectColumn.setHeaderRenderer((table12, value, isSelected, hasFocus, row, column) -> selectAllCheckBox);

        // 테이블을 스크롤 패널에 추가
        JScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(500, 500));
         */

        // 전체 패널에 라벨과 스크롤 패널 추가
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(500, 500));
        dialogPanel.add(descriptionLabel, BorderLayout.NORTH);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

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