package edu.metasync.demo.repository;

import edu.metasync.demo.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    RefreshTokenEntity findByToken(String token);

    void deleteByToken(String token);
}
