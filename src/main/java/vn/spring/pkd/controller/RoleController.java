package vn.spring.pkd.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import vn.spring.pkd.dto.request.ApiResponse;
import vn.spring.pkd.dto.request.role.CreateRoleRequest;
import vn.spring.pkd.dto.request.role.UpdateRoleRequest;
import vn.spring.pkd.dto.response.RoleResponse;
import vn.spring.pkd.service.role.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody CreateRoleRequest createRoleRequest){
        return  ApiResponse.<RoleResponse>builder()
                .result(roleService.create(createRoleRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<RoleResponse> getById(@PathVariable() String id){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<RoleResponse> update(@PathVariable String id, @RequestBody UpdateRoleRequest updateRoleRequest){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.update(id,updateRoleRequest))
                .build();
    }



    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id){
        roleService.delete(id);
        return ApiResponse.<Void>builder().build();
    }
}
