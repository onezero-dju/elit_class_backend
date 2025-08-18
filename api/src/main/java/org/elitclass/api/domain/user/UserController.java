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
import org.elitclass.db.usertoken.UserTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;

    @GetMapping("/user")
    public ResponseEntity<?> me(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @CookieValue(value = "accessToken", required = false) String accessTokenCookie,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            HttpServletResponse response
    ) {
        // 1) Bearer 헤더 우선
        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        } else {
            accessToken = accessTokenCookie; // 쿠키도 허용(보조)
        }

        // 2) access 토큰 유효하면 바로 사용자 반환
        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            Long userId = jwtProvider.getUserId(accessToken);
            return ResponseEntity.ok(userRepository.findById(userId).orElseThrow());
        }

        // 3) (선택) refresh 쿠키가 있다면 새 access 발급해서 반환/세팅
        if (refreshTokenCookie != null && jwtProvider.validateToken(refreshTokenCookie)) {
            Long userId = jwtProvider.getUserId(refreshTokenCookie);
            UserEntity u = userRepository.findById(userId).orElseThrow();

            String newAccess = jwtProvider.createAccessToken(
                    u.getId(), u.getEmail(), u.getRole() != null ? u.getRole().name() : null
            );

            // 쿠키 방식으로 재발급만 할 거면:
            CookieUtil.addJwtCookie(response, "accessToken", newAccess, 60 * 30);
            return ResponseEntity.ok(u);

            /* 만약 프론트(localStorage)를 갱신시키고 싶다면 위 대신 아래처럼 바디에 담아주고
               프론트에서 res.data.accessToken 있으면 localStorage 업데이트:
            return ResponseEntity.ok(Map.of("user", u, "accessToken", newAccess));
            */
        }

        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletResponse response
    ) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String access = authorization.substring(7);
            if (jwtProvider.validateToken(access)) {
                Long userId = jwtProvider.getUserId(access);
                userRepository.findById(userId).ifPresent(u -> {
                    userTokenRepository.findByUser(u).ifPresent(userTokenRepository::delete); // DB 토큰 제거
                });
            }
        }
        // 쿠키도 비워주기(있는 경우)
        CookieUtil.addJwtCookie(response, "accessToken", "", 0);
        CookieUtil.addJwtCookie(response, "refreshToken", "", 0);

        return ResponseEntity.noContent().build();
    }

}
