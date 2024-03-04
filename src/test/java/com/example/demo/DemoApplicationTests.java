package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class DemoApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

	@Autowired
	private TestEntityRepository repository;
	@Autowired
	private SortEntityRepository sortRepository;

	@BeforeEach
    void cleanDatabase() {
		repository.deleteAll();
		sortRepository.deleteAll();
	}

	@Test
	void testDirectArrayPosition() {
		repository.saveAllAndFlush(List.of(
				TestEntity.builder().value(TestEnum.ONE).valueList(List.of(TestEnum.ONE, TestEnum.TWO)).build(),
				TestEntity.builder().value(TestEnum.ONE).valueList(List.of(TestEnum.TWO, TestEnum.THREE)).build()
		));

		final var result = repository.findDirect();

		assertThat(result).hasSize(1);
	}

	@Test
	void testSubqueryArrayPosition() {
		sortRepository.saveAllAndFlush(List.of(
				SortEntity.builder().enumKey(TestEnum.ONE).sortValue("EINS").build(),
				SortEntity.builder().enumKey(TestEnum.TWO).sortValue("ZWEI").build(),
				SortEntity.builder().enumKey(TestEnum.THREE).sortValue("DREI").build()
		));

		repository.saveAllAndFlush(List.of(
				TestEntity.builder().value(TestEnum.ONE).build(),
				TestEntity.builder().value(TestEnum.TWO).build(),
				TestEntity.builder().value(TestEnum.THREE).build()
		));

		final var result = repository.findSubquery();

		assertThat(result)
				.extracting(TestEntity::getValue)
				// "DREI", "EINS", "ZWEI"
				.containsExactly(TestEnum.THREE, TestEnum.ONE, TestEnum.TWO);
	}
}
