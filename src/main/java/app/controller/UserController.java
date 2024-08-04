package app.controller;

import app.entity.User;
import app.model.UpdateUserRequest;
import app.model.UserResponse;
import app.model.WebResponse;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse>getUser(User user) {
        UserResponse userResponse = userService.getUser(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(path = "current",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> updateUser(User user, @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateUser(user, updateUserRequest);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
