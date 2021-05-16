package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.config.ApplicationConfiguration;
import de.tlg.trainingsplaner.resourceserver.helper.Helper;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(path= ApplicationConfiguration.USER_COLLECTION_BASE_PATH)
public class UserCollectionController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserCollectionController.class);

    // read application port from 'application.properties'
    @Value("${server.port}")
    public int APPLICATION_PORT;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> registerNewUser (@RequestHeader(name = "authorization") String accessToken,
                                                   @RequestBody UserRegisterRequest userRegisterRequest) {
        if (Helper.accessTokenInvalid(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // prevent email or userId from being used more than once
        if (userRepository.findUserByEmail(userRegisterRequest.getEmail()) != null
                || userRepository.findUserByUserId(userRegisterRequest.getUserId()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // save user in db
        userRepository.save(UserTransformer.transformUserRegisterRequestToUser(userRegisterRequest));

        ResponseEntity<String> result = null;

        try {
            // prepare response header
            URI locationURI = new URI(String.format("localhost:%d%s/%s", this.APPLICATION_PORT,
                    ApplicationConfiguration.USER_ITEM_BASE_PATH, userRegisterRequest.getUserId()));
            result = ResponseEntity.created(locationURI).build();
        } catch(URISyntaxException exception) {
            exception.printStackTrace();
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @GetMapping
    public ResponseEntity<String> get() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PutMapping
    public ResponseEntity<String> put() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PatchMapping
    public ResponseEntity<String> patch() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @DeleteMapping
    public ResponseEntity<String> delete() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
