package com.hhplus.dependenciesversionhelper.ui;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UIComponentManager {
    public DefaultTableModel createTableModel(String relativePath, String springBootVersion) {
        return new DefaultTableModel(new Object[]{"Select", "Dependencies(" + relativePath + ")(Spring Boot Version: " + springBootVersion + ")"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 첫 번째 열(체크박스)만 편집 가능
            }
        };
    }

    @NotNull
    public JLabel createLabel() {
        // 라벨 여백 조정
        JLabel descriptionLabel = new JLabel("<html><b>SpringBoot Version Managed Dependencies.</b><br><br>"
                + "The following dependencies are managed by SpringBoot, so specifying a version is unnecessary.<br>"
                + "If you wish for versions to be automatically managed, please select the checkboxes.<br></html>");
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // 상하 여백은 유지하고 좌우 여백을 제거합니다.
        return descriptionLabel;
    }

    // 이미 만들어진 tableModel 과 dependency 이용해 테이블 객체 생성해 반환
    public static JTable createTable(DefaultTableModel tableModel, List<Dependency> dependencies) {
        // 의존성 데이터를 테이블 모델에 추가
        for (Dependency dependency : dependencies) {
            tableModel.addRow(new Object[]{false, dependency.deserialize()});
        }

        JTable table = new JBTable(tableModel);
        setupTableHeaderCheckbox(table);
        setupColumnWidths(table);

        return table;
    }

    private static void setupTableHeaderCheckbox(JTable table) {
        TableColumn selectColumn = table.getColumnModel().getColumn(0);
        JCheckBox selectAllCheckBox = new JCheckBox();
        selectAllCheckBox.setHorizontalAlignment(JLabel.CENTER);
        selectAllCheckBox.setBorderPainted(true);
        selectColumn.setHeaderRenderer((table1, value, isSelected, hasFocus, row, column) -> selectAllCheckBox);

        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelectAll((DefaultTableModel) table.getModel(), selectAllCheckBox, header, e);
            }
        });
    }

    // 외부에서 tableModel 을 받아 작업을 처리하도록 변경
    private static void toggleSelectAll(DefaultTableModel tableModel, JCheckBox selectAllCheckBox, JTableHeader header, MouseEvent e) {
        // 클릭된 위치가 첫 번째 열의 헤더인지 확인
        int columnIndex = header.columnAtPoint(e.getPoint());
        if (columnIndex == 0) {
            // 체크박스의 선택 상태 토글
            boolean isSelected = !selectAllCheckBox.isSelected();
            selectAllCheckBox.setSelected(isSelected);

            // 모든 행의 체크박스 상태 업데이트
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(isSelected, i, 0);
            }
            tableModel.fireTableDataChanged();
        }
    }

    private static void setupColumnWidths(JTable table) {
        TableColumn selectColumn = table.getColumnModel().getColumn(0);
        selectColumn.setPreferredWidth(30);
        selectColumn.setMaxWidth(30);
        selectColumn.setMinWidth(30);
    }
}
