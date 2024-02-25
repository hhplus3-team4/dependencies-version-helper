package com.hhplus.dependenciesversionhelper.util;

import java.util.regex.Pattern;

public interface PatternManager {

    public Pattern getAllDependenciesMatchPattern();

    public String getRemoveDependenciesMatchPattern(String groupId, String artifactId);

    public String getDependencyRemovalReplacementPattern(String groupId, String artifactId);

    public String getAddVersionDependenciesMatchPattern(String groupId, String artifactId);

    public String getDependencyAddVersionReplacementPattern(String groupId, String artifactId, String version);
}
