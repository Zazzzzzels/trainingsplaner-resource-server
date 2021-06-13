package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.config.URLConfig;
import de.tlg.trainingsplaner.resourceserver.db.UserDatabase;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.RequestValidator;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import io.swagger.v3.oas.annotations.Operation;
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

/**
 * RestController class for the user collection resource
 */
@RestController
@RequestMapping(path= URLConfig.USER_COLLECTION_PATH)
public class UserCollectionController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserCollectionController.class);

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private UserDatabase userDatabase;

    @Autowired
    private UserTransformer userTransformer;

    @Operation(summary = "register new user in database", operationId = "registerNewUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "user information was created", content = @Content),
            @ApiResponse(responseCode = "401", description = "client is unauthorized to request user info", content = @Content),
            @ApiResponse(responseCode = "409", description = "given mail and/or id is already registered", content = @Content),
            @ApiResponse(responseCode = "500", description = "unexpected error", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> registerNewUser (@RequestBody UserRegisterRequest userRegisterRequest) {

        // this call is expected to come from the authorization server
        // TODO: authentication for auth server itself will be implemented later

        // validate incoming data and return bad request if validation fails
        if(requestValidator.isBadUserRegisterRequest(userRegisterRequest)) {
            LOGGER.info("Bad user register request: {}.", userRegisterRequest);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // prevent email from being used more than once
        if (this.userDatabase.findUserByEmail(userRegisterRequest.getEmail()) != null) {
            LOGGER.info("Email '{}' is already used for an existing account.", userRegisterRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // transform request to db user model and save in db
        // saveNewUser(...) creates the user id which is used in response creation
        User user = this.userDatabase.saveNewUser(this.userTransformer.transformUserRegisterRequestToUser(userRegisterRequest));

        ResponseEntity<String> result;

        try {
            // prepare response header
            final URI location = new URI(String.format("%s/%s", URLConfig.USER_COLLECTION_PATH, user.getUserId()));
            result = ResponseEntity.created(location).build();
        } catch(URISyntaxException exception) {
            LOGGER.error("Error while creating URI for response." );
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("User '{}' successfully created.", user.getUserId());

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
