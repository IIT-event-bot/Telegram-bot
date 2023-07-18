package com.project.userService.services.student;

import com.project.studentService.StudentServiceOuterClass;
import com.project.userService.models.*;
import com.project.userService.repository.StudentRepository;
import com.project.userService.services.group.GroupService;
import com.project.userService.services.user.UserService;
import com.project.userService.services.notification.TelegramNotificationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends com.project.studentService.StudentServiceGrpc.StudentServiceImplBase
        implements StudentService {
    private final StudentRepository repository;
    private final GroupService groupService;
    private final UserService userService;
    private final TelegramNotificationService notificationService;
    private final StudentDtoMapper studentMapper;

    @Override
    public StudentDto getStudentById(long id) {
        var student = repository.getStudentById(id);
        return studentMapper.convert(student);
    }

    @Override
    @Transactional
    public void saveStudent(Student student) {
        repository.save(student);
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return repository.findAll()
                .stream()
                .map(studentMapper::convert)
                .toList();
    }

    @Override
    public List<StudentDto> getStudentsByGroup(String groupName) {
        if (groupName == null) {
            return getAllStudents();
        }
        var group = groupService.getGroupsLikeTitle(groupName);
        List<Long> groupsId = group.stream().map(Group::getId).toList();
        return repository.getStudentsByGroupId(groupsId)
                .stream()
                .map(studentMapper::convert)
                .toList();
    }

    @Override
    @Transactional
    public void deleteStudentById(long studentId) {
        var student = repository.getStudentById(studentId);
        userService.updateUserRole(student.getUserId(), Role.USER);
        repository.deleteStudentById(studentId);
        notificationService.sendNotification(student.getUserId(),
                "Удаление из системы",
                student.getSurname() + " " + student.getName() + " " + student.getPatronymic() + ", " +
                        "вы были удалены из системы информирования");
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
        var student = repository.getStudentById(studentId);
        var user = userService.getUserById(student.getUserId());

        var response = StudentServiceOuterClass.UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getStudentById(StudentServiceOuterClass.StudentRequest request,
                               StreamObserver<StudentServiceOuterClass.StudentResponse> responseObserver) {
        var studentId = request.getStudentId();
        var student = repository.getStudentById(studentId);

        var response = StudentServiceOuterClass.StudentResponse.newBuilder()
                .setId(student.getId())
                .setName(student.getName())
                .setSurname(student.getSurname())
                .setPatronymic(student.getPatronymic())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
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
                    .setUsername(user.getUsername())
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getStudentByUserId(StudentServiceOuterClass.UserRequest request,
                                   StreamObserver<StudentServiceOuterClass.StudentResponse> responseObserver) {
        var userId = request.getId();
        var student = repository.getStudentByUserId(userId);

        var response = StudentServiceOuterClass.StudentResponse.newBuilder()
                .setId(student.getId())
                .setName(student.getName())
                .setSurname(student.getSurname())
                .setPatronymic(student.getPatronymic())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
