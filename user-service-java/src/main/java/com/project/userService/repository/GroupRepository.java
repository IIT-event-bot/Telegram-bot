package com.project.userService.repository;

import com.project.userService.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group getGroupById(long id);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from groups
            where title like '%'||:#{#title}||'%'
            """, nativeQuery = true)
    List<Group> getGroupByTitle(String title);

    @Modifying
    @Transactional
    void deleteGroupById(long groupId);
}
