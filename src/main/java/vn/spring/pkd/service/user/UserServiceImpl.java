package vn.spring.pkd.service.user;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.spring.pkd.dto.request.AssignRoleRequest;
import vn.spring.pkd.dto.request.user.CreateUserRequest;
import vn.spring.pkd.dto.request.user.UpdateUserRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.dto.response.RoleSimpleResponse;
import vn.spring.pkd.dto.response.UserResponse;
import vn.spring.pkd.entity.Role;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.entity.UserRole;
import vn.spring.pkd.repository.RoleRepository;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;
import vn.spring.pkd.service.jwt.JwtService;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.valid-duration}")
    private long validDuration;


    @Override
    public AuthenticationResponse register(CreateUserRequest createUserRequest) {
        if(userRepository.checkUserName(createUserRequest.getUsername())){
            throw new RuntimeException("username trùng");
        }

        User user = User.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .username(createUserRequest.getUsername())
                .password(createUserRequest.getPassword())
                .dateOfBirth(createUserRequest.getDateOfBirth())
                .build();

        userRepository.save(user);

        // Generate and return JWT token
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token, true);
    }

    @Override
    public UserResponse create(CreateUserRequest createUserRequest) {
        if(userRepository.checkUserName(createUserRequest.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));

        userRepository.save(user);

        return toUserResponse(user, List.of());
    }

    @Override
    public UserResponse update(String id, UpdateUserRequest updateUserRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setDateOfBirth(updateUserRequest.getDateOfBirth());
        userRepository.save(user);

        List<RoleSimpleResponse> roles = getUserRoles(user.getId());

        return toUserResponse(user, roles);

    }

    @Override
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // mỗi user đc xem thông tin của mình, k đc xem của ng khác
    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public UserResponse getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RoleSimpleResponse> roles = getUserRoles(id);
        return toUserResponse(user, roles);
    }

    //admin mới đc get list user
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> toUserResponse(user, getUserRoles((user.getId()))))
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
        // B1: Lấy list UserRole theo userId
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);

        // B2: Trích ra danh sách roleId
        List<String> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        // B3: Lấy list Role từ các roleId
        List<Role> roles = roleRepository.findAllById(roleIds);

        // B4: Map Role → RoleSimpleResponse rồi trả về
        return roles.stream()
                .map(role -> RoleSimpleResponse.builder()
                        .id(role.getId())
                        .code(role.getRoleCode())   // getRoleCode() → field "code"
                        .name(role.getRoleName())   // getRoleName()  → field "name"
                        .build())
                .toList();
    }

    private UserResponse toUserResponse(User user, List<RoleSimpleResponse> roleSimpleResponses){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .dateOfBirth(user.getDateOfBirth())
                .roles(roleSimpleResponses)
                .build();
    }



}
