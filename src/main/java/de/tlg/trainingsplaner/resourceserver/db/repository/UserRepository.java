package de.tlg.trainingsplaner.resourceserver.db.repository;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByEmail(String email);
    User findUserByUserId(String userId);
}
