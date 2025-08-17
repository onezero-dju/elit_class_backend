package org.elitclass.api.domain.user;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.cookie.CookieUtil;
import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.jwt.JwtProvider;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<?> me(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            Long userId = jwtProvider.getUserId(accessToken);
            return ResponseEntity.ok(userRepository.findById(userId).orElseThrow());
        }
        if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
            Long userId = jwtProvider.getUserId(refreshToken);
            UserEntity u = userRepository.findById(userId).orElseThrow();
            String newAccess = jwtProvider.createAccessToken(u.getId(), u.getEmail(),
                    u.getRole() != null ? u.getRole().name() : null);
            CookieUtil.addJwtCookie(response, "accessToken", newAccess, 60 * 30);
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.status(401).build();
    }
}
