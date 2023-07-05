package com.project.user_service.repository;

import com.project.user_service.models.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {
    Statement getStatementById(long id);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from statements
            where is_checked = :#{#isChecked}
            """, nativeQuery = true)
    List<Statement> getStatementByChecked(boolean isChecked);
}
