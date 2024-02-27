package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.List;

public interface GradleParser {
    public List<Dependency> parseGradleDependencies(PsiFile psiFile, String gradleFileName);

}
