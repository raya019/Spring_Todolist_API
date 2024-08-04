package app.service;

import app.entity.User;
import app.model.UpdateUserRequest;
import app.model.UserResponse;
import app.repository.UserRepository;
import app.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(User user ) {
//        String email = jwtService.extractUsername(JwtToken);
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "Unauthorized"
//                ));
//
//        if (!jwtService.isTokenValid(JwtToken, user)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
//        }

        return UserResponse
                .builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(User user,UpdateUserRequest updateUserRequest) {
        validationUtil.validate(updateUserRequest);

        if (Objects.nonNull(updateUserRequest.getName())) {
            user.setName(updateUserRequest.getName());
        }
        if (Objects.nonNull(updateUserRequest.getPhone())) {
            user.setPhone(updateUserRequest.getPhone());
        }
        if (Objects.nonNull(updateUserRequest.getEmail())) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (Objects.nonNull(updateUserRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        userRepository.save(user);

        return UserResponse
                .builder()
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
