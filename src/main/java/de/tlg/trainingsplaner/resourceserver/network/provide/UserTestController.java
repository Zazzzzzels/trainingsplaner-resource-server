package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.config.URLConfig;
import de.tlg.trainingsplaner.resourceserver.db.UserDatabase;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is only used for local test purposed. Check of accessToken is not done here.
 */
@RestController
@RequestMapping(path = URLConfig.USER_RESOURCE_TEST_PATH)
public class UserTestController {

    @Autowired
    private UserDatabase userDatabase;

    @Autowired
    private UserTransformer userTransformer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addMultipleUsers(@RequestBody List<UserRegisterRequest> userRegisterRequests) {
        for(UserRegisterRequest userRegisterRequest : userRegisterRequests) {
            this.userDatabase.saveNewUser(this.userTransformer.transformUserRegisterRequestToUser(userRegisterRequest));
        }
        return ResponseEntity.ok(String.format("added %d users", userRegisterRequests.size()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserInfoResponse>> getAllUserInfo() {

        Iterable<User> userIterable = this.userDatabase.findAll();
        List<UserInfoResponse> userList = new ArrayList<>();

        userIterable.forEach(user -> userList.add(this.userTransformer.transformUserToUserInfoResponse(user)));

        return ResponseEntity.ok(userList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> clearDatabase() {
        this.userDatabase.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
