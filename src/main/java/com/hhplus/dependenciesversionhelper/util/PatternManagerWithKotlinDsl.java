package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public class PatternManagerWithKotlinDsl implements PatternManager{
    @Override
    public Pattern getVersionPattern() {
        return Pattern.compile("id\\(\"org\\.springframework\\.boot\"\\)\\s+version\\s+\"([^\"]+)\"");
    }

    @Override
    public Pattern getDependencyPattern() {
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
        String groupArtifactVersionPattern = "['\"]([^':]+):([^':]+):([^'\"\\s]+)['\"]";

        return Pattern.compile("(" + dependencyTypes + ")\\s*\\(" + groupArtifactVersionPattern + "\\)");
    }

    @Override
    public String getDependencyCleanPattern(String groupId, String artifactId) {
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

        return  "(" + dependencyTypes + ")\\(\\\""
                + Pattern.quote(groupId)
                + ":" + Pattern.quote(artifactId) + ":[^\\\"]+\\\"\\)";    }

    @Override
    public String getDependencyReplacementPattern(String groupId, String artifactId) {
        return "$1(\"" + groupId + ":" + artifactId + "\")";
    }
}
