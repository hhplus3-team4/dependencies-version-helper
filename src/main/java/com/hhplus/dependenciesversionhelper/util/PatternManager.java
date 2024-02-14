package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public interface PatternManager {

    public Pattern getVersionPattern();

    public Pattern getDependencyPattern();

    public String getDependencyCleanPattern(String groupId, String artifactId);

    public String getDependencyReplacementPattern(String groupId, String artifactId);
}
