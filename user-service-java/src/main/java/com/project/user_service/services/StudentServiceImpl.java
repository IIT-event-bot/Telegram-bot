package com.project.user_service.services;

import com.project.user_service.models.Group;
import com.project.user_service.models.Role;
import com.project.user_service.models.Student;
import com.project.user_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository repository;
    private final GroupService groupService;
    private final UserService userService;

    @Override
    public Student getStudentById(long id) {
        return repository.getStudentById(id);
    }

    @Override
    public void saveStudent(Student student) {
        repository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Override
    public List<Student> getStudentsByGroup(String groupName) {
        if (groupName == null) {
            return getAllStudents();
        }
        var group = groupService.getGroupsByTitle(groupName);
        List<Long> groupsId = group.stream().map(Group::getId).toList();
        return repository.getStudentsByGroupsId(groupsId);
    }

    @Override
    @Transactional
    public void deleteStudentById(long studentId) {
        var student = repository.getStudentByGroupId(studentId);
        userService.updateUserRole(student.getUserId(), Role.STUDENT);
        repository.deleteStudentById(studentId);
    }

    @Override
    public void updateStudent(Student student) {
        repository.save(student);
    }
}
