package app.service;

import app.entity.User;
import app.model.UpdateUserRequest;
import app.model.UserResponse;

public interface UserService {
    UserResponse getUser(User user);
    UserResponse updateUser(User user, UpdateUserRequest updateUserRequest);
}
