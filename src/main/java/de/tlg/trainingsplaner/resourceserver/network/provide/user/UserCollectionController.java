package de.tlg.trainingsplaner.resourceserver.network.provide.user;

import de.tlg.trainingsplaner.resourceserver.config.ApplicationConfiguration;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.network.consume.AuthServerConsument;
import de.tlg.trainingsplaner.resourceserver.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(path= ApplicationConfiguration.USER_COLLECTION_BASE_PATH)
public class UserCollectionController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserCollectionController.class);

    @Autowired
    UserRepository userRepository;

    AuthServerConsument authServerConsument = new AuthServerConsument();

    @Operation(summary = "register new user in database", operationId = "registerNewUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "user information was created", content = @Content),
            @ApiResponse(responseCode = "401", description = "client is unauthorized to request user info", content = @Content),
            @ApiResponse(responseCode = "409", description = "given mail and/or id is already registered", content = @Content),
            @ApiResponse(responseCode = "500", description = "unexpected error", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> registerNewUser (@Parameter(description = "access token")
                                                       @RequestHeader(name = "authorization") String accessToken,
                                                   @RequestBody UserRegisterRequest userRegisterRequest) {
        if (authServerConsument.accessTokenInvalid(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // prevent email or userId from being used more than once
        if (userRepository.findUserByEmail(userRegisterRequest.getEmail()) != null
                || userRepository.findUserByUserId(userRegisterRequest.getUserId()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // save user in db
        userRepository.save(UserTransformer.transformUserRegisterRequestToUser(userRegisterRequest));

        ResponseEntity<String> result;

        try {
            // prepare response header
            URI locationURI = new URI(String.format("%s/%s", ApplicationConfiguration.USER_COLLECTION_BASE_PATH,
                    userRegisterRequest.getUserId()));
            result = ResponseEntity.created(locationURI).build();
        } catch(URISyntaxException exception) {
            exception.printStackTrace();
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @Operation(summary = "not allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "method not allowed", content = @Content)
    })
    @GetMapping
    public ResponseEntity<String> get() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Operation(summary = "not allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "method not allowed", content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> put() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Operation(summary = "not allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "method not allowed", content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> patch() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Operation(summary = "not allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "method not allowed", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<String> delete() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
