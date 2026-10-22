package vn.tnteco.study.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import vn.tnteco.study.exception.ErrorCode;
import vn.tnteco.study.exception.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final List<HandlerMapping> handlerMappings;

    // Tự khởi tạo trực tiếp kèm module hỗ trợ LocalDateTime (JSR-310)
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Constructor CHỈ inject handlerMappings qua @Lazy, không cần autowire ObjectMapper
    public JwtAuthenticationEntryPoint(@Lazy List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        boolean handlerExists = false;
        if (handlerMappings != null) {
            for (HandlerMapping handlerMapping : handlerMappings) {
                try {
                    HandlerExecutionChain handler = handlerMapping.getHandler(request);
                    if (handler != null) {
                        handlerExists = true;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (!handlerExists) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(ErrorCode.PATH_NOT_FOUND.getCode())
                    .message(ErrorCode.PATH_NOT_FOUND.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(ErrorCode.UNAUTHENTICATED.getCode())
                    .message(ErrorCode.UNAUTHENTICATED.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
