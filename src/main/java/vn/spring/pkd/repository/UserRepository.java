package vn.spring.pkd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.spring.pkd.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("""
    SELECT COUNT(u) > 0
    FROM User u
    WHERE u.username = :username
""")
    boolean checkUserName(@Param("username") String username);

    Optional<User> findByUsername(String username);

}
