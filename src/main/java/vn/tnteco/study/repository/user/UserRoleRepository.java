package vn.tnteco.study.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tnteco.study.model.UserRole;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    // Lấy trực tiếp danh sách tên Role của một User thông qua bảng trung gian thuần
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<String> findRoleIdsByUserId(@Param("userId") String userId);
    void deleteByUserId(String userId);
}
