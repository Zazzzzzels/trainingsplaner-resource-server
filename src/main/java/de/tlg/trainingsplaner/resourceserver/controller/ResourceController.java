package de.tlg.trainingsplaner.resourceserver.controller;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@Controller
@RequestMapping(path="/trainingsplaner/resource-server")
public class ResourceController {

    @Value("${server.port}")
    private int port;

    private final String baseURLLocalhost = "localhost:" + port + "/trainingsplaner/resource-server/users/";

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/users")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader(name = "authorization") String accessToken,
                                                        @RequestParam(name = "uid") String userId) {
        if (accessTokenInvalid(accessToken, userId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findUserByEmail(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(UserTransformer.transformUserToUserInfoResponse(user));
    }

    @PostMapping(path = "/users")
    public ResponseEntity<String> registerNewUser (@RequestHeader(name = "authorization") String accessToken,
                                                   @RequestBody UserRequest userRequest) {
        if (accessTokenInvalid(accessToken, userRequest.getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // prevent email from being used more than once
        if (userRepository.findUserByEmail(userRequest.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User dbUser = UserTransformer.transformUserRequestToUser(userRequest);

        userRepository.save(dbUser);

        ResponseEntity<String> result = null;

        try {
            URI locationURI = new URI(baseURLLocalhost + dbUser.getId().toString());
            result = ResponseEntity.created(locationURI).build();
        } catch(URISyntaxException exception) {
            exception.printStackTrace();
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    // TODO: check db --> has user valid access token?
    private boolean accessTokenInvalid(String accessToken, String userId) {
        final String invalidToken = "Bearer unauthorized";
        // workaround to test if program return UNAUTHORIZED if access token is not valid:
        // userId is currently the email address --> needed to check access token

        return invalidToken.equalsIgnoreCase(accessToken);
    }
}
