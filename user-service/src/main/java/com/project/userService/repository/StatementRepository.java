package com.project.userService.repository;

import com.project.userService.models.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {
    Statement getStatementById(long id);

    @Transactional
    @Query(value = """
            select *
            from statements
            where user_id = :#{#userId}
              and is_checked = false
            """, nativeQuery = true)
    Statement getUncheckedStatementByUserId(long userId);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from statements
            where is_checked = :#{#isChecked}
            """, nativeQuery = true)
    List<Statement> getStatementByChecked(boolean isChecked);
}
