package vn.spring.pkd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.spring.pkd.entity.UserRole;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
//    existsByUserIdAndRoleId()
@Query("""
    SELECT COUNT(ur) > 0
    FROM UserRole ur
    WHERE ur.userId = :userId
    AND ur.roleId = :roleId
""")
boolean checkUserAndRole(@Param("userId") String userId, @Param("roleId") String roleId);

//    findByUserId()
List<UserRole> findByUserId(String userId);

//    findByRoleId()
List<UserRole> findByRoleId(String roleId);

/////////////////////////////////////////////////////////////////////////////
// lấy roleCode theo username
    @Query("""
            SELECT r.roleCode
            FROM UserRole ur, User u, Role r
            WHERE ur.userId = u.id
              AND ur.roleId = r.id
              AND u.username = :username
            """)
//    Một user có thể có nhiều role nên kiểu trả về là List<String>
    List<String> findRoleCodesByUsername(@Param("username") String username);

//    cách viết inner join
//    @Query("""
//        SELECT r.roleCode
//        FROM UserRole ur
//        JOIN User u ON ur.userId = u.id
//        JOIN Role r ON ur.roleId = r.id
//        WHERE u.username = :username
//        """)
//    List<String> findRoleCodesByUsername(@Param("username") String username);

//////////////////////////////////////////////////////////////////////////////////////////
//    deleteByUserIdAndRoleId()
//    countByRoleId()
}
