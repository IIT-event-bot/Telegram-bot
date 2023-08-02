package com.project.userService.repository;

import com.project.userService.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query(value = """
            select *
            from session
            where token = :#{#token}
              and is_expired = false
            """, nativeQuery = true)
    Session getNotExpiredSessionByToken(String token);

    @Query(value = """
            select *
            from session
            where user_id = :#{#userId}
              and is_expired = false
            """, nativeQuery = true)
    List<Session> getNotExpiredSessionByUserId(long userId);
}
