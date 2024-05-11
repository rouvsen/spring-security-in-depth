package az.ingress.repository;

import az.ingress.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    default User findUserByEmail(String email) {
        return new User(email, "12345", "FIRST_NAME", "LAST_NAME");
    }
}
