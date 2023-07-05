package com.project.userService.services;

import com.project.studentService.StudentServiceOuterClass;
import com.project.userService.models.Group;
import com.project.userService.models.Role;
import com.project.userService.models.Student;
import com.project.userService.models.User;
import com.project.userService.repository.StudentRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends com.project.studentService.StudentServiceGrpc.StudentServiceImplBase implements StudentService {
    private final StudentRepository repository;
    private final GroupService groupService;
    private final UserService userService;

    @Override
    public Student getStudentById(long id) {
        return repository.getStudentById(id);
    }

    @Override
    @Transactional
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
        return repository.getStudentsByGroupId(groupsId);
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

    @Transactional
    @Override
    public void getUserByStudentId(StudentServiceOuterClass.StudentRequest request,
                                   StreamObserver<StudentServiceOuterClass.UserResponse> responseObserver) {
        var studentId = request.getStudentId();
        var student = getStudentById(studentId);
        var user = userService.getUserById(student.getUserId());

        var response = StudentServiceOuterClass.UserResponse.newBuilder()
                .setId(user.getId())
                .setChatId(user.getChatId())
                .setUsername(user.getUsername())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Transactional
    public void getStudentsChatIdByGroupId(StudentServiceOuterClass.GroupRequest request,
                                           StreamObserver<StudentServiceOuterClass.UserResponse> responseObserver) {
        var groupId = request.getGroupId();
        var students = repository.getStudentsByGroupId(groupId);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            var student = students.get(i);
            users.add(userService.getUserById(student.getUserId()));
        }
        for (int i = 0; i < users.size(); i++) {
            var user = users.get(i);
            var response = StudentServiceOuterClass.UserResponse.newBuilder()
                    .setId(user.getId())
                    .setChatId(user.getChatId())
                    .setUsername(user.getUsername())
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }
}
