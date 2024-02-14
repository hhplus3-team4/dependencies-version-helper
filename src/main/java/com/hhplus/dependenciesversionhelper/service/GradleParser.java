package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.intellij.psi.PsiFile;

import java.util.List;

public interface GradleParser {
    public String findSpringBootVersion(PsiFile psiFile, String gradleFileName);

    public List<Dependency> parseGradleDependencies(PsiFile psiFile, String gradleFileName);

}
