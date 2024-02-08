package com.hhplus.dependenciesversionhelper;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;

import java.util.List;
import java.util.regex.Pattern;

public class GradleFileEditor {

    public static void removeDependencyVersion(Project project, List<Dependency> selectedDependencies) {
        VirtualFile file = findBuildGradleFile(project);

        if (file != null) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                String fileContent = document.getText();

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    String modifiedContent = fileContent;

                    for (Dependency dependency : selectedDependencies) {
                        // 선택된 의존성에 대해서만 버전 정보 제거
                        String patternString = "implementation '" + Pattern.quote(dependency.getGroupId()) + ":" + Pattern.quote(dependency.getArtifactId()) + ":[^']+'";
                        modifiedContent = modifiedContent.replaceAll(patternString,
                                "implementation '" + dependency.getGroupId() + ":" + dependency.getArtifactId() + "'");
                    }

                    if (!modifiedContent.equals(fileContent)) {
                        // 문서 내용 업데이트
                        document.setText(modifiedContent);
                        System.out.println("build.gradle 파일 업데이트 완료");

                        // 문서와 PSI(프로그램 구조 인터페이스) 동기화
                        PsiDocumentManager.getInstance(project).commitDocument(document);
                    }
                });
                // 수정 완료된 File 을 로드해 파일 시스템에 저장한다.
                FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(file));
            }
        }
    }

    private static VirtualFile findBuildGradleFile(Project project) {
        // 현재 열린 파일 확인
        VirtualFile[] currentFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (currentFiles.length > 0 && "build.gradle".equals(currentFiles[0].getName())) {
            return currentFiles[0]; // 현재 열린 파일이 build.gradle이면 리턴
        } else {
            // 프로젝트 디렉토리에서 build.gradle 파일 찾기
            VirtualFile projectBaseDir = ProjectUtil.guessProjectDir(project);
            if (projectBaseDir != null) {
                VirtualFile[] buildGradleFiles = VfsUtil.collectChildrenRecursively(projectBaseDir)
                        .stream()
                        .filter(file -> !file.isDirectory() && "build.gradle".equals(file.getName()))
                        .toArray(VirtualFile[]::new);

                if (buildGradleFiles.length > 0) {
                    return buildGradleFiles[0]; // 첫 번째 찾은 build.gradle 파일 리턴
                }
            }
        }
        return null; // build.gradle 파일을 찾지 못한 경우
    }
}
