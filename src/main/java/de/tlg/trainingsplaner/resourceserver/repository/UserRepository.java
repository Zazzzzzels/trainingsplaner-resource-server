package de.tlg.trainingsplaner.resourceserver.repository;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByEmail(String email);
}
