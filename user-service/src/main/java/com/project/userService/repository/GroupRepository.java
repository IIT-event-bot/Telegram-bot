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
            where upper(title) like '%' || upper(:#{#title}) || '%'
            """, nativeQuery = true)
    List<Group> getGroupLikeTitle(String title);

    @Modifying
    @Transactional
    void deleteGroupById(long groupId);

    Group getGroupByTitle(String title);

    @Modifying
    @Transactional
    @Query(value = """
            select *
            from groups
            where id in :#{#ids}
            """, nativeQuery = true)
    List<Group> getGroupsById(List<Long> ids);
}
