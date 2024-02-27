package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public class PatternManagerWithGroovy implements PatternManager{

    @Override
    public Pattern getAllDependenciesMatchPattern() {
        String dependencyTypes = String.join("|",
                "implementation",
                "testImplementation",
                "compileOnly",
                "runtimeOnly",
                "testRuntimeOnly",
                "testCompileOnly",
                "api",
                "annotationProcessor",
                "developmentOnly"
        );

        String groupArtifactVersionPattern = "['\"]([^:']+)[:]?([^:']*?)(?::([^']+))?['\"]";

        return Pattern.compile("(" + dependencyTypes + ")\\s+" + groupArtifactVersionPattern, Pattern.DOTALL);
    }

    @Override
    public String getRemoveDependenciesMatchPattern(String groupId, String artifactId) {
        String dependencyTypes = String.join("|",
                "implementation",
                "testImplementation",
                "compileOnly",
                "runtimeOnly",
                "testRuntimeOnly",
                "testCompileOnly",
                "api",
                "annotationProcessor",
                "developmentOnly"
        );

        return  "(" + dependencyTypes + ")\\s+'\"?" + Pattern.quote(groupId) +
                ":" + Pattern.quote(artifactId) + ":[^'\"\\s]+['\"]?";
    }

    @Override
    public String getDependencyRemovalReplacementPattern(String groupId, String artifactId) {
        return "$1 '" + groupId + ":" + artifactId + "'";
    }

    @Override
    public String getAddVersionDependenciesMatchPattern(String groupId, String artifactId) {
        String dependencyTypes = String.join("|",
                "implementation",
                "testImplementation",
                "compileOnly",
                "runtimeOnly",
                "testRuntimeOnly",
                "testCompileOnly",
                "api",
                "annotationProcessor",
                "developmentOnly"
        );

        // 버전 정보가 없는 의존성 선언을 찾는 패턴
        return  "(" + dependencyTypes + ")\\s+'\"?" + Pattern.quote(groupId) +
                ":" + Pattern.quote(artifactId) + "'\"?";
    }

    @Override
    public String getDependencyAddVersionReplacementPattern(String groupId, String artifactId, String version) {
        return "$1 '" + groupId + ":" + artifactId + ":" + version + "'";
    }
}
