package org.example.dockerdemo.repository;

import org.example.dockerdemo.entity.TestUser;
import org.example.dockerdemo.enums.DeleteFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, String> {

    Optional<TestUser> findByIdAndDeleteFlag(String id, DeleteFlag deleteFlag);

    Optional<TestUser> findByUsernameAndDeleteFlag(String username, DeleteFlag deleteFlag);

    Page<TestUser> findByDeleteFlag(DeleteFlag deleteFlag, Pageable pageable);
}
