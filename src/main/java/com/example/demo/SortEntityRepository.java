package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SortEntityRepository extends JpaRepository<SortEntity, UUID> {

}
