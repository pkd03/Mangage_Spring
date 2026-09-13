package vn.spring.pkd.service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.spring.pkd.config.Endpoints;
import vn.spring.pkd.dto.request.ApiResponse;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("GET")) {
            return Arrays.stream(Endpoints.PUBLIC_GET_ENDPOINT)
                    .anyMatch(pattern -> pathMatcher.match(pattern, uri));
        }

        if (method.equals("POST")) {
            return Arrays.stream(Endpoints.PUBLIC_POST_ENDPOINT)
                    .anyMatch(pattern -> pathMatcher.match(pattern, uri));
        }

        if (method.equals("PUT")) {
            return Arrays.stream(Endpoints.PUBLIC_PUT_ENDPOINT)
                    .anyMatch(pattern -> pathMatcher.match(pattern, uri));
        }

        if (method.equals("DELETE")) {
            return Arrays.stream(Endpoints.PUBLIC_DELETE_ENDPOINT)
                    .anyMatch(pattern -> pathMatcher.match(pattern, uri));
        }

        return false;    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request, response);
        if (!StringUtils.hasText(token)) {
            sendErrorResponse(request, response);
            return;
        }
        String username = jwtService.extractUsername(token);
        if(username == null){
            sendErrorResponse(request, response);
           return;
        }
        if( SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return null;
        }

        if (!authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7).trim();

        if (!StringUtils.hasText(token)) {
            return null;
        }

        return token;

    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> responseApi = ApiResponse.builder()
                .code(401)
                .message("Yêu cầu đăng nhập").build();
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseApi));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
