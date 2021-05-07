package de.tlg.trainingsplaner.resourceserver.controller;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRequest;
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

    @PostMapping(path = "/users/")
    public ResponseEntity<String> registerNewUser (@RequestBody UserRequest userRequest, @RequestParam String accessToken) {
        if(!accessTokenValid(accessToken, userRequest.getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // prevent email from being used more than once
        if (userRepository.findUserByEmail(userRequest.getEmail()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User dbUser = new User();
        dbUser.setFirstName(userRequest.getFirstName());
        dbUser.setLastName(userRequest.getLastName());
        dbUser.setEmail(userRequest.getEmail());

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

    private static boolean accessTokenValid(String accessToken, String email) {
        // TODO: check db --> has user valid access token?
        // workaround to test if program return UNAUTHORIZED if access token is not valid:
        return !"unauthorized".equalsIgnoreCase(accessToken);
    }
}
