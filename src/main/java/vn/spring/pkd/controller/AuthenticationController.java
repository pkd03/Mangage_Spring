package vn.spring.pkd.controller;

import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.spring.pkd.dto.request.ApiResponse;
import vn.spring.pkd.dto.request.auth.AuthenticationRequest;
import vn.spring.pkd.dto.request.auth.IntrospectRequest;
import vn.spring.pkd.dto.response.AuthenticationResponse;
import vn.spring.pkd.dto.response.IntrospectResponse;
import vn.spring.pkd.service.auth.AuthenticationService;
import vn.spring.pkd.service.auth.AuthenticationServiceIml;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/login/v2")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticateV2(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        ApiResponse<AuthenticationResponse> response =  ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }
    AuthenticationServiceIml authenticationServiceIml;
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result = authenticationServiceIml.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

}
