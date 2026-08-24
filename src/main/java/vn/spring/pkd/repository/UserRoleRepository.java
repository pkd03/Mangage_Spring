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
//    deleteByUserIdAndRoleId()
//    countByRoleId()
}
