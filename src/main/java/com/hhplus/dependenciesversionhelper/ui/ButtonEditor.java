package com.hhplus.dependenciesversionhelper.ui;

import com.hhplus.dependenciesversionhelper.model.Dependency;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.List;

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private boolean isPushed;
    private List<Dependency> dependencies;
    private int currentRow;

    public ButtonEditor(JCheckBox checkBox, List<Dependency> dependencies) {
        super(checkBox);
        this.dependencies = dependencies;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        currentRow = row;
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            isPushed = false;
            Dependency dependency = dependencies.get(currentRow);
            String url = "https://mvnrepository.com/artifact/" +
                    dependency.getGroupId() + "/" + dependency.getArtifactId();

            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return "find version";
    }
}
