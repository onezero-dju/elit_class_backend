package org.elitclass.api.config.oauth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.cookie.CookieUtil;
import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.oauth2.CustomClientRegistrationRepo;
import org.elitclass.api.oauth2.CustomOAuth2AuthorizedClientService;
import org.elitclass.api.oauth2.handler.OAuth2SuccessHandler; // Import 추가
import org.elitclass.api.user.service.CustomOAuth2UserService;
import org.elitclass.db.usertoken.UserTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer{

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // 주입 추가
    private final UserTokenRepository userTokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.csrf((csrf) -> csrf.disable());
        http.formLogin((login) -> login.disable());
        http.httpBasic((httpBasic) -> httpBasic.disable());
        http.oauth2Login((oauth2) -> oauth2
                .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.clientRegistrationRepository()))
                .userInfoEndpoint((userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService)))
                .successHandler(oAuth2SuccessHandler));

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("accessToken", "refreshToken") // 브라우저에 바로 만료 Set-Cookie
                .logoutSuccessHandler((req, res, auth) -> {
                    // (선택) DB의 refreshToken도 제거
                    if (auth != null && auth.getPrincipal() instanceof CustomOAuth2User u) {
                        userTokenRepository.findByUser(u.getUserEntity())
                                .ifPresent(userTokenRepository::delete);
                    }
                })
        );

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/open-api/**", "/login",
                        "/uploads/**", "/api/class/all","/api/**","/api/user", "/oauth2/**").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }



    // CORS 설정 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 대해 CORS 적용
        return source;
    }




}
