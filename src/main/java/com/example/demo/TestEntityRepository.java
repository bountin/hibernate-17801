package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface TestEntityRepository extends JpaRepository<TestEntity, UUID> {

    @Query("""
                FROM TestEntity te
                WHERE array_position(te.valueList, te.value) = 1
            """)
    Collection<TestEntity> findDirect();

    @Query("""
                FROM TestEntity te
                ORDER BY array_position(
                  array((SELECT se.enumKey FROM SortEntity se ORDER BY se.sortValue))
                  , te.value)
            """)
    Collection<TestEntity> findSubquery();
}
