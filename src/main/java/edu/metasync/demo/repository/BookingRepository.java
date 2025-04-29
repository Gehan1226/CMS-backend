package edu.metasync.demo.repository;

import edu.metasync.demo.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findAllByUserId(Long userId);
}
