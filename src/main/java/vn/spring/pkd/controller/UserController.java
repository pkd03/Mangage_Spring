package vn.spring.pkd.controller;

import ch.qos.logback.classic.Logger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.spring.pkd.dto.request.ApiResponse;
import vn.spring.pkd.dto.request.AssignRoleRequest;
import vn.spring.pkd.dto.request.role.CreateRoleRequest;
import vn.spring.pkd.dto.request.user.CreateUserRequest;
import vn.spring.pkd.dto.request.user.UpdateUserRequest;
import vn.spring.pkd.dto.response.RoleResponse;
import vn.spring.pkd.dto.response.RoleSimpleResponse;
import vn.spring.pkd.dto.response.UserResponse;
import vn.spring.pkd.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> create(@RequestBody CreateUserRequest createUserRequest){
        return  ApiResponse.<UserResponse>builder()
                .result(userService.create(createUserRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAll(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody UpdateUserRequest updateUserRequest){
        return ApiResponse.<UserResponse>builder()
                .result(userService.update(id, updateUserRequest))
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<UserResponse> getById(@PathVariable String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getById(id))
                .build();
    }
///////////////////////////////////////////////////////
/// controller user-role
    @GetMapping("/{userId}/roles")
    public List<RoleSimpleResponse> getUserRoles(@PathVariable String userId){
        return  userService.getUserRoles(userId);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable String userId, @RequestBody AssignRoleRequest assignRoleRequest){
        userService.assignRole(userId,assignRoleRequest);

        return  ResponseEntity.noContent().build();
    }

//    @PostMapping("/{userId}/roles")
//    ApiResponse<Void> assignRole(@PathVariable String userId, AssignRoleRequest assignRoleRequest){
//        userService.assignRole(userId, assignRoleRequest);
//
//    }

////////////////////////////////////////////////////////

}
