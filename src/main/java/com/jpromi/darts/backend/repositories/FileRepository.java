package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByIdAndIsDeletedFalse(Long id);
    Optional<File> findByUuidAndIsDeletedFalse(UUID uuid);
    Optional<File> findByPathAndIsDeletedFalse(String path);
}
