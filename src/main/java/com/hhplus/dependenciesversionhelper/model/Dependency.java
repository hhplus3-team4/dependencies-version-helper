package com.hhplus.dependenciesversionhelper.model;

public class Dependency {
    private String dependencyType;
    private String groupId;
    private String artifactId;
    private String version;

    public Dependency(String dependencyType, String groupId, String artifactId, String version) {
        this.dependencyType = dependencyType;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return dependencyType + "{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                "}";
    }

    public String deserialize() {
        return dependencyType + "(" + String.format("%s:%s:%s", groupId, artifactId, version) + ")";
    }
}
