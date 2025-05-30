package edu.metasync.demo.repository;

import edu.metasync.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserName(String username);
    boolean existsByUserName(String username);
}
