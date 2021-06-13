package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.config.URLConfig;
import de.tlg.trainingsplaner.resourceserver.db.UserDatabase;
import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.RequestValidator;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserCollectionControllerTest {

    @Mock
    private UserDatabase userDatabase;

    @Mock
    private UserTransformer userTransformer;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private UserCollectionController userCollectionController;

    private static UserRegisterRequest userRegisterRequestAllPossibleValues;
    private static User transformedRegistrationUserAllPossibleValues;
    private static User transformedAndSavedRegistrationUserAllPossibleValues;

    private static UserRegisterRequest userRegisterRequestMissingRequiredValues;
    private static UserRegisterRequest userRegisterRequestInvalidEmailFormat;

    private static UserRegisterRequest userRegisterRequestNoGender;
    private static User transformedRegistrationUserNoGender;
    private static User transformedAndSavedRegistrationUserNoGender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setup() {

        userRegisterRequestAllPossibleValues = new UserRegisterRequest.Builder()
                .firstName("Jonas")
                .lastName("Hennecke")
                .email("jonas@gmail.com")
                .gender(GenderEnum.MALE)
                .build();

        transformedRegistrationUserAllPossibleValues = new UserTransformer().transformUserRegisterRequestToUser(userRegisterRequestAllPossibleValues);

        transformedAndSavedRegistrationUserAllPossibleValues = new User.Builder()
                .userId(transformedRegistrationUserAllPossibleValues.getUserId())
                .firstName(transformedRegistrationUserAllPossibleValues.getFirstName())
                .lastName(transformedRegistrationUserAllPossibleValues.getLastName())
                .gender(transformedRegistrationUserAllPossibleValues.getGender())
                .email(transformedRegistrationUserAllPossibleValues.getEmail())
                .build();

        transformedAndSavedRegistrationUserAllPossibleValues.setId(12345);

        userRegisterRequestInvalidEmailFormat = new UserRegisterRequest.Builder()
                .firstName("Christophe")
                .lastName("Lemaitre")
                .gender(GenderEnum.MALE)
                .email("wrongmail@")
                .build();

        userRegisterRequestMissingRequiredValues = new UserRegisterRequest.Builder()
                .firstName("Jimmi")
                .gender(GenderEnum.MALE)
                .build();

        userRegisterRequestInvalidEmailFormat = new UserRegisterRequest.Builder()
                .firstName("Christophe")
                .lastName("Lemaitre")
                .gender(GenderEnum.MALE)
                .email("wrongmail@")
                .build();

        userRegisterRequestNoGender = new UserRegisterRequest.Builder()
                .firstName("Jimmi")
                .lastName("Hendrix")
                .email("jimmihendrix@woodstock.com")
                .build();

        transformedRegistrationUserNoGender = new UserTransformer().transformUserRegisterRequestToUser(userRegisterRequestAllPossibleValues);

        transformedAndSavedRegistrationUserNoGender= new User.Builder()
                .userId(transformedRegistrationUserNoGender.getUserId())
                .firstName(transformedRegistrationUserNoGender.getFirstName())
                .lastName(transformedRegistrationUserNoGender.getLastName())
                .gender(transformedRegistrationUserNoGender.getGender())
                .email(transformedRegistrationUserNoGender.getEmail())
                .build();
    }

    @Test
    @DisplayName("register a new user with all possible values and return created")
    public void shouldRegisterNewUserAllPossibleValuesAndReturnOK() {
        String awaitedLocationHeaderString = URLConfig.USER_COLLECTION_PATH
                .concat("/")
                .concat(transformedRegistrationUserAllPossibleValues.getUserId());

        when(userTransformer.transformUserRegisterRequestToUser(userRegisterRequestAllPossibleValues)).thenReturn(transformedRegistrationUserAllPossibleValues);
        when(userDatabase.saveNewUser(transformedRegistrationUserAllPossibleValues)).thenReturn(transformedAndSavedRegistrationUserAllPossibleValues);

        ResponseEntity<String> response = userCollectionController.registerNewUser(userRegisterRequestAllPossibleValues);
        List<String> locationHeader = response.getHeaders().get("location");

        verify(userTransformer, times(1)).transformUserRegisterRequestToUser(userRegisterRequestAllPossibleValues);
        verify(userDatabase, times(1)).saveNewUser(transformedRegistrationUserAllPossibleValues);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(locationHeader).isNotNull();
        assertThat(locationHeader.size()).isEqualTo(1);
        assertThat(locationHeader.get(0)).isEqualTo(awaitedLocationHeaderString);
    }

    @Test
    @DisplayName("register a new user without optional attribute gender and return created")
    public void shouldRegisterNewUserNoGenderAttributeAndReturnOK() {
        String awaitedLocationHeaderString = URLConfig.USER_COLLECTION_PATH
                .concat("/")
                .concat(transformedRegistrationUserNoGender.getUserId());

        when(userTransformer.transformUserRegisterRequestToUser(userRegisterRequestNoGender)).thenReturn(transformedRegistrationUserNoGender);
        when(userDatabase.saveNewUser(transformedRegistrationUserNoGender)).thenReturn(transformedAndSavedRegistrationUserNoGender);

        ResponseEntity<String> response = userCollectionController.registerNewUser(userRegisterRequestNoGender);
        List<String> locationHeader = response.getHeaders().get("location");

        verify(userTransformer, times(1)).transformUserRegisterRequestToUser(userRegisterRequestNoGender);
        verify(userDatabase, times(1)).saveNewUser(transformedRegistrationUserNoGender);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(locationHeader).isNotNull();
        assertThat(locationHeader.size()).isEqualTo(1);
        assertThat(locationHeader.get(0)).isEqualTo(awaitedLocationHeaderString);
    }

    @Test
    @DisplayName("try to register existing user")
    public void shouldNotRegisterUserAndReturnConflict() {
        final String mail = "jonas@gmail.com";

        when(userDatabase.findUserByEmail(mail)).thenReturn(new User.Builder().email(mail).build());

        ResponseEntity<String> response = userCollectionController.registerNewUser(userRegisterRequestAllPossibleValues);
        List<String> locationHeader = response.getHeaders().get("location");

        verify(userDatabase, times(1)).findUserByEmail(mail);

        assertThat(locationHeader).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("try to register a user with missing required attributes")
    public void shouldNotRegisterUserAndReturnBadRequestMissingAttributes() {
        when(requestValidator.isBadUserRegisterRequest(userRegisterRequestMissingRequiredValues)).thenReturn(true);

        ResponseEntity<String> response = userCollectionController.registerNewUser(userRegisterRequestMissingRequiredValues);
        List<String> locationHeader = response.getHeaders().get("location");

        verify(requestValidator, times(1)).isBadUserRegisterRequest(userRegisterRequestMissingRequiredValues);

        assertThat(locationHeader).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("try to register a user by sending an invalid email address")
    public void shouldNotRegisterUserAndReturnBadRequestInvalidEmail() {
        when(requestValidator.isBadUserRegisterRequest(userRegisterRequestInvalidEmailFormat)).thenReturn(true);

        ResponseEntity<String> response = userCollectionController.registerNewUser(userRegisterRequestInvalidEmailFormat);
        List<String> locationHeader = response.getHeaders().get("location");

        verify(requestValidator, times(1)).isBadUserRegisterRequest(userRegisterRequestInvalidEmailFormat);

        assertThat(locationHeader).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("try to call get")
    public void callGetShouldReturnNotAllowed() {
        ResponseEntity<String> response = userCollectionController.get();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("try to call put")
    public void callPutShouldReturnNotAllowed() {
        ResponseEntity<String> response = userCollectionController.put();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("try to call patch")
    public void callPatchShouldReturnNotAllowed() {
        ResponseEntity<String> response = userCollectionController.patch();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("try to call delete")
    public void callDeleteShouldReturnNotAllowed() {
        ResponseEntity<String> response = userCollectionController.delete();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
