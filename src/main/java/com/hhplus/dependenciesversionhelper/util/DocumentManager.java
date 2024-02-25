package com.hhplus.dependenciesversionhelper.util;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;

public class DocumentManager {

    public void updateContent(Project project, Document document, String modifiedContent) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.setText(modifiedContent);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }

    public void saveContent(VirtualFile gradleFile) {
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(gradleFile);
        if (document != null) {
            fileDocumentManager.saveDocument(document);
        }
    }
}
