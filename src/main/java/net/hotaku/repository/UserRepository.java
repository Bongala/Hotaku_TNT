package net.hotaku.repository;

import net.hotaku.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /****
 * Retrieves a user by their username.
 *
 * @param username the username to search for
 * @return an Optional containing the user if found, or empty if not found
 */
Optional<User> findByUsername(String username);
    /****
 * Retrieves a user by their email address.
 *
 * @param email the email address to search for
 * @return an Optional containing the user if found, or empty if no user exists with the given email
 */
Optional<User> findByEmail(String email);
    /****
 * Checks whether a user exists with the specified username.
 *
 * @param username the username to check for existence
 * @return true if a user with the given username exists, false otherwise
 */
Boolean existsByUsername(String username);
    /****
 * Checks if a user exists with the specified email address.
 *
 * @param email the email address to check for existence
 * @return true if a user with the given email exists, false otherwise
 */
Boolean existsByEmail(String email);
} 