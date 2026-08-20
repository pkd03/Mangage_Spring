package vn.spring.pkd.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.spring.pkd.dto.request.role.CreateRoleRequest;
import vn.spring.pkd.dto.request.role.UpdateRoleRequest;
import vn.spring.pkd.dto.response.RoleDetailResponse;
import vn.spring.pkd.dto.response.RoleResponse;
import vn.spring.pkd.entity.Role;

import java.util.List;

public interface RoleService {

    RoleResponse create(CreateRoleRequest createRoleRequest);

    RoleResponse update(String id, UpdateRoleRequest updateRoleRequest);

//k xóa role nếu role đang đc user dùng-> countByRoleId()
    void delete(String id);

    RoleResponse getById(String id);

    List<RoleResponse> getAll();

    RoleDetailResponse getDetail(String id);
}
