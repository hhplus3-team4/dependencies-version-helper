package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public class PatternManagerWithKotlinDsl implements PatternManager{

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
                "kapt",
                "developmentOnly"
        );

        String groupArtifactVersionPattern = ")\\(\\s*\"([^:]+):([^:\"]+)(?::([^\"\\)]+))?\"\\s*\\)";

        return Pattern.compile("(" + dependencyTypes + groupArtifactVersionPattern, Pattern.DOTALL);
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
                "kapt",
                "developmentOnly"
        );

        return  "(" + dependencyTypes + ")\\(\\\""
                + Pattern.quote(groupId)
                + ":" + Pattern.quote(artifactId) + ":[^\\\"]+\\\"\\)";
    }

    @Override
    public String getDependencyRemovalReplacementPattern(String groupId, String artifactId) {
        return "$1(\"" + groupId + ":" + artifactId + "\")";
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
                "kapt",
                "developmentOnly"
        );

        return  "(" + dependencyTypes + ")\\(\\s*\""
                + Pattern.quote(groupId)
                + ":" + Pattern.quote(artifactId) + "\"\\s*\\)";
    }

    @Override
    public String getDependencyAddVersionReplacementPattern(String groupId, String artifactId) {
        return "$1(\"" + groupId + ":" + artifactId + ":Need_Version\")";
    }
}
