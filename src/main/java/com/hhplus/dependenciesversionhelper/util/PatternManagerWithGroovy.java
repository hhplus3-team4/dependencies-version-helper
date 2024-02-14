package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public class PatternManagerWithGroovy implements PatternManager{
    @Override
    public Pattern getVersionPattern() {
        return Pattern.compile("id 'org\\.springframework\\.boot' version '([^']+)'");
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

        String groupArtifactVersionPattern = "['\"]([^:']+)[:]?([^:']*?)(?::([^']+))?['\"]";

        return Pattern.compile("(" + dependencyTypes + ")\\s+" + groupArtifactVersionPattern, Pattern.DOTALL);
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

        return  "(" + dependencyTypes + ")\\s+'\"?" + Pattern.quote(groupId) +
                ":" + Pattern.quote(artifactId) + ":[^'\"\\s]+['\"]?";
    }

    @Override
    public String getDependencyReplacementPattern(String groupId, String artifactId) {
        return "$1 '" + groupId + ":" + artifactId + "'";
    }

}
