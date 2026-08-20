package vn.spring.pkd.service.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.spring.pkd.dto.request.AssignRoleRequest;
import vn.spring.pkd.dto.request.user.CreateUserRequest;
import vn.spring.pkd.dto.request.user.UpdateUserRequest;
import vn.spring.pkd.dto.response.RoleSimpleResponse;
import vn.spring.pkd.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest createUserRequest);

    UserResponse update(String id, UpdateUserRequest updateUserRequest);

    void delete(String id);

    UserResponse getById(String id);

    List<UserResponse> getAll();

    void assignRole(String userId, AssignRoleRequest request);

//    void removeRole(String userId, String roleId);

    List<RoleSimpleResponse> getUserRoles(String userId);
}
