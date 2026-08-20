package vn.spring.pkd.service.role;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.spring.pkd.dto.request.role.CreateRoleRequest;
import vn.spring.pkd.dto.request.role.UpdateRoleRequest;
import vn.spring.pkd.dto.response.RoleDetailResponse;
import vn.spring.pkd.dto.response.RoleResponse;
import vn.spring.pkd.entity.Role;
import vn.spring.pkd.entity.User;
import vn.spring.pkd.repository.RoleRepository;
import vn.spring.pkd.repository.UserRepository;
import vn.spring.pkd.repository.UserRoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;

    UserRepository userRepository;

    UserRoleRepository userRoleRepository;

    @Override
    public RoleResponse create(CreateRoleRequest createRoleRequest) {
        Role role = Role.builder()
                .roleCode(createRoleRequest.getRoleCode())
                .roleName(createRoleRequest.getRoleName())
                .build();

        roleRepository.save(role);
        return toResponse(role);
    }

    @Override
    public RoleResponse update(String id, UpdateRoleRequest updateRoleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setRoleName(updateRoleRequest.getRoleName());
        roleRepository.save(role);
        return toResponse(role);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(role);

    }

    @Override
    public RoleResponse getById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(role);
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RoleDetailResponse getDetail(String id) {
        return null;
    }

//    toResponse viết riêng
    private RoleResponse toResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .createAt(role.getCreateAt())
                .updateAt(role.getUpdateAt())
                .deleteAt(role.getDeleteAt())
                .build();
    }
}
