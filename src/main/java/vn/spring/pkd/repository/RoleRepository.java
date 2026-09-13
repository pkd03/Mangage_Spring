package vn.spring.pkd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.spring.pkd.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Object> findByRoleName(String customer);
//    Optional<Role> findByCode(String code);
//
//    Page<Role> findByNameContainingIgnoreCase(
//            String name,
//            Pageable pageable
//    );
//findByCode() findByName() existsByCode() findAllByIsDeletedFalse()
}
