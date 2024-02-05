package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.wizard.WizardAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class SampleDialogWrapper extends DialogWrapper {

    private List<Dependency> dependencies = new ArrayList<>();

    public SampleDialogWrapper(List<Dependency> dependencies) {
        super(true); // use current window as parent
        this.dependencies = dependencies;
        // 창 타이틀
        setTitle("Test DialogWrapper");
        init();
    }

    @Override
    protected Action[] createActions() { // 버튼 생성
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

        // 창 체크박스
        for (Dependency dependency : dependencies) {
            JCheckBox checkBox = new JCheckBox(dependency.deserialize());
            checkBox.setPreferredSize(new Dimension(10, 10));
            dialogPanel.add(checkBox, BorderLayout.CENTER);
        }

        return dialogPanel;
    }

    protected final class OkAction extends DialogWrapperAction {

        protected OkAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            // todo: ok 누른 후 실행할 코드
            // checkbox checked 된것 리스트 담기
            // 위치 찾아서 변경하기
        }
    }

    protected final class CancelAction extends DialogWrapperAction {

        protected CancelAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent e) {
            dispose();
        }
    }



}