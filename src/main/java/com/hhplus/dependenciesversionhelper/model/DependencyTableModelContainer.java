package com.hhplus.dependenciesversionhelper.model;

import javax.swing.table.DefaultTableModel;

public class DependencyTableModelContainer {
    private DefaultTableModel versionedModel;
    private DefaultTableModel versionlessModel;

    public DependencyTableModelContainer(DefaultTableModel versionedModel, DefaultTableModel versionlessModel) {
        this.versionedModel = versionedModel;
        this.versionlessModel = versionlessModel;
    }

    public DefaultTableModel getVersionedModel() {
        return versionedModel;
    }

    public DefaultTableModel getVersionlessModel() {
        return versionlessModel;
    }
}
