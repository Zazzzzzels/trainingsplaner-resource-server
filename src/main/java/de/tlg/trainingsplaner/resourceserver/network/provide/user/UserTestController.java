package de.tlg.trainingsplaner.resourceserver.network.provide.user;

import de.tlg.trainingsplaner.resourceserver.config.ApplicationConfiguration;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = ApplicationConfiguration.USER_RESOURCE_TEST_BASE_PATH)
public class UserTestController {

    @Autowired
    UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addMultipleUsers(@RequestBody List<UserRegisterRequest> userRegisterRequests) {
        for(UserRegisterRequest userRegisterRequest : userRegisterRequests) {
            userRegisterRequest.setUserId(UUID.randomUUID().toString());
            userRepository.save(UserTransformer.transformUserRegisterRequestToUser(userRegisterRequest));
        }
        return ResponseEntity.ok(String.format("added %d users", userRegisterRequests.size()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserInfoResponse>> getAllUserInfo() {

        Iterable<User> users = userRepository.findAll();

        List<UserInfoResponse> userList = new ArrayList<>();

        for(User user : users) {
            userList.add(UserTransformer.transformUserToUserInfoResponse(user));
        }

        return ResponseEntity.ok(userList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> clearDatabase() {
        userRepository.deleteAll();
        return ResponseEntity.ok("all users deleted");
    }
}
