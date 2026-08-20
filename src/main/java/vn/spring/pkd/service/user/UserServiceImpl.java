package vn.spring.pkd.service.user;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.spring.pkd.dto.request.AssignRoleRequest;
import vn.spring.pkd.dto.request.user.CreateUserRequest;
import vn.spring.pkd.dto.request.user.UpdateUserRequest;
import vn.spring.pkd.dto.response.RoleResponse;
import vn.spring.pkd.dto.response.RoleSimpleResponse;
import vn.spring.pkd.dto.response.UserResponse;
import vn.spring.pkd.entity.Role;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.entity.UserRole;
import vn.spring.pkd.repository.RoleRepository;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;


    @Override
    public UserResponse create(CreateUserRequest createUserRequest) {
        if(userRepository.checkUserName(createUserRequest.getUsername())){
            throw new RuntimeException("Username already exists");
        }
//
//        User user = User.builder()
//                .firstName(createUserRequest.getFirstName())
//                .lastName(createUserRequest.getLastName())
//                .username(createUserRequest.getUsername())
//                .password(createUserRequest.getPassword())
//                .dateOfBirth(createUserRequest.getDateOfBirth())
//                .build();

//        Role role = roleRepository.findById(createUserRequest.getRoleId())
//                .orElseThrow();
//
        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());

        userRepository.save(user);

        return toResponse(user);
    }

    @Override
    public UserResponse update(String id, UpdateUserRequest updateUserRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setDateOfBirth(updateUserRequest.getDateOfBirth());
        userRepository.save(user);

        return toResponse(user);

    }

    @Override
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void assignRole(String userId, AssignRoleRequest assignRoleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(assignRoleRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if(userRoleRepository.checkUserAndRole(user.getId(), role.getId())){
            throw new RuntimeException("User đã có Role này");
        }
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setCreateAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
    }

//    @Override
//    public void removeRole(String userId, String roleId) {
//
//    }

    @Override
    public List<RoleSimpleResponse> getUserRoles(String userId) {

        return List.of();
    }

    private UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }


}
