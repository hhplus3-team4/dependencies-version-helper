package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DependencyComparatorTest {

    private DependencyComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new DependencyComparator();
    }

    @Test
    void testCompareWithEmptyLists() {
        // 두 의존성 목록이 모두 비어 있는 경우
        List<Dependency> changeDependencies = comparator.compareWithDependencyManager(new ArrayList<>(), new ArrayList<>());
        assertTrue(changeDependencies.isEmpty(), "두 의존성 목록이 모두 비어 있을 때는 변경이 필요한 의존성 목록도 비어 있어야 합니다.");
    }

    @Test
    void testCompareWithMatchingDependencies() {
        // `projectDependencies`와 `pomDependencies`에 일치하는 의존성이 있는 경우
        List<Dependency> projectDependencies = Arrays.asList(
                new Dependency("compile", "com.example", "example-project", "1.0.0"));
        List<Dependency> pomDependencies = Arrays.asList(
                new Dependency("compile", "com.example", "example-project", "1.0.0"));

        List<Dependency> changeDependencies = comparator.compareWithDependencyManager(projectDependencies, pomDependencies);
        assertFalse(changeDependencies.isEmpty(), "일치하는 의존성이 있을 때는 변경이 필요한 의존성 목록이 비어 있지 않아야 합니다.");
        assertEquals(1, changeDependencies.size(), "정확히 일치하는 하나의 의존성만 변경이 필요한 목록에 포함되어야 합니다.");
    }

    @Test
    void testDependencyWithEmptyVersionIsNotIncluded() {
        // 버전 번호가 비어 있는 `projectDependencies` 의존성이 있는 경우
        List<Dependency> projectDependencies = Arrays.asList(
                new Dependency("compile", "com.example", "example-project", ""));
        List<Dependency> pomDependencies = Arrays.asList(
                new Dependency("compile", "com.example", "example-project", "1.0.0"));

        List<Dependency> changeDependencies = comparator.compareWithDependencyManager(projectDependencies, pomDependencies);
        assertTrue(changeDependencies.isEmpty(), "버전 번호가 비어 있는 프로젝트 의존성은 변경이 필요한 목록에 포함되지 않아야 합니다.");
    }
}
