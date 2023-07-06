package com.project.userService.services;

import com.project.userService.UserServiceOuterClass;
import com.project.userService.models.Role;
import com.project.userService.models.User;
import com.project.userService.repository.RoleRepository;
import com.project.userService.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends com.project.userService.UserServiceGrpc.UserServiceImplBase implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Override
    public void getUserById(UserServiceOuterClass.UserRequest request,
                            StreamObserver<UserServiceOuterClass.UserResponse> responseObserver) {
        var userId = request.getId();
        var user = getUserById(userId);

        var response = UserServiceOuterClass.UserResponse.newBuilder()
                .setId(user.getId())
                .setChatId(user.getChatId())
                .setUsername(user.getUsername())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public User getUserById(long id) {
        return repository.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public void updateUserRole(long userId, Role role) {
        var user = repository.getUserById(userId);
        var savedRole = roleRepository.getRoleDtoByName(role);
        user.setRole(savedRole);
        repository.save(user);
    }

    @Override
    public void saveUser(User user) {
        var savedUser = getUserByChatId(user.getChatId());
        if (savedUser != null) {
            return;
        }
        var userRole = roleRepository.getRoleDtoByName(Role.USER);
        user.setRole(userRole);
        repository.save(user);
    }

    @Override
    public User getUserByChatId(long chatId) {
        return repository.getUserByChatId(chatId);
    }
}
