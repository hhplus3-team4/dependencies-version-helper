package com.hhplus.dependenciesversionhelper.service;

import com.hhplus.dependenciesversionhelper.model.Dependency;
import com.hhplus.dependenciesversionhelper.model.DependencyAnalyzer;
import com.hhplus.dependenciesversionhelper.util.DependenciesFetcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class DependencyComparatorTest {
    DependenciesFetcher dependenciesFetcher =  new DependenciesFetcher();
    DependencyComparator dependencyComparator = new DependencyComparator();

    @Test
    @DisplayName("spring boot가 manage 하는 dependency 중 version이 기입되어 있는 의존성과 manage 하지 않는 dependency 중 version이 없는 의존성을 반환한다.")
    void compareWithDependencyManagerTest() {
        //given
        List<Dependency> pomDependencies = dependenciesFetcher.fetchSpringBootDependenciesPOM("3.2.0");

        List<Dependency> projectDependencies = Arrays.asList(
        //versioned managed dependencies
            new Dependency("implementation","org.apache.activemq","activemq-amqp","1.0.0"),
            new Dependency("compileOnly","org.apache.activemq","activemq-jdbc-store","1.0.0"),
            new Dependency("developmentOnly","org.crac","crac","1.0.0"),
            new Dependency("annotationProcessor","org.flywaydb","flyway-firebird","1.0.0"),

        //versionless managed dependencies
            new Dependency("testImplementation","org.apache.kafka","connect-json",""),
            new Dependency("implementation","org.apache.kafka","connect-mirror",""),

        //versionless unmanaged dependencies
            new Dependency("compileOnly","io.github.stylesmile","fastboot-core",""),
            new Dependency("developmentOnly","org.kill-bill.billing.plugin","killbill-plugin-api-notification",""),

        //versioned unmanaged dependencies
            new Dependency("annotationProcessor","org.tomitribe","tomitribe-crest","1.0.0"),
            new Dependency("testImplementation","com.adobe.testing","s3mock-junit5","1.0.0")
        );

        //when
        DependencyAnalyzer analyzer = dependencyComparator.compareWithDependencyManager(projectDependencies, pomDependencies);

        //then
        assertThat(analyzer.getVersionedManagedDependencies()).hasSize(4)
                .extracting("groupId", "artifactId")
                .containsExactlyInAnyOrder(
                        tuple("org.apache.activemq", "activemq-amqp"),
                        tuple("org.apache.activemq", "activemq-jdbc-store"),
                        tuple("org.crac", "crac"),
                        tuple("org.flywaydb", "flyway-firebird")
                );

        assertThat(analyzer.getVersionlessUnmanagedDependencies()).hasSize(2)
                .extracting("groupId", "artifactId")
                .containsExactlyInAnyOrder(
                        tuple("io.github.stylesmile", "fastboot-core"),
                        tuple("org.kill-bill.billing.plugin", "killbill-plugin-api-notification")
                );
    }
}