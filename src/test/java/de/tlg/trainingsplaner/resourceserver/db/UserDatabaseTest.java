package de.tlg.trainingsplaner.resourceserver.db;

import de.tlg.trainingsplaner.resourceserver.db.repository.UserRepository;
import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDatabaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDatabase userDatabase;

    private User user1;
    private User user2;
    private User user3;
    private User[] userInDB;
    private Iterable<User> userIterableContainingAllUsers;

    private User userToSave;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User.Builder()
                .userId("A")
                .firstName("Jimmi")
                .lastName("Hendrix")
                .email("jimihendrix@live.com")
                .gender(GenderEnum.MALE)
                .build();

        user2 = new User.Builder()
                .userId("B")
                .firstName("John")
                .lastName("Mayer")
                .email("johnymayer@gmail.com")
                .gender(null)
                .build();

        user3 = new User.Builder()
                .userId("C")
                .firstName("Peter")
                .lastName("Gabriel")
                .email("sledge@hammer.com")
                .gender(GenderEnum.MALE)
                .build();

        userInDB = new User[] {user1, user2, user3};
        userIterableContainingAllUsers = Arrays.asList(userInDB);

        userToSave = new User.Builder()
                .firstName("Dan")
                .lastName("Bastille")
                .email("dan@bastille.uk")
                .gender(GenderEnum.MALE)
                .build();

    }

    @Test
    @DisplayName("find all users from db")
    public void shouldReturnListOfAllUsers() {
        when(userRepository.findAll()).thenReturn(userIterableContainingAllUsers);

        List<User> users = userDatabase.findAll();

        assertThat(users.size()).isEqualTo(userInDB.length);
        assertThat(users).contains(user1, user2, user3);
    }
}

