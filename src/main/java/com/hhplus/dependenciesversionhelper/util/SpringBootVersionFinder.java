package com.hhplus.dependenciesversionhelper.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

public class SpringBootVersionFinder {

    public String find(Project project) {
        LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);

        for (Library library : libraryTable.getLibraries()) {
            if (library != null) {
                String libraryName = library.getName();

                if (libraryName != null && libraryName.startsWith("Gradle: org.springframework.boot:spring-boot:")) {
                    int versionStart = libraryName.lastIndexOf(':') + 1;
                    return libraryName.substring(versionStart);
                }
            }
        }
        return null;
    }
}
