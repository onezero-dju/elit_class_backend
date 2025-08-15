package org.elitclass.api.config.oauth;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.oauth2.CustomClientRegistrationRepo;
import org.elitclass.api.oauth2.CustomOAuth2AuthorizedClientService;
import org.elitclass.api.oauth2.handler.OAuth2SuccessHandler; // Import 추가
import org.elitclass.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // 주입 추가

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> csrf.disable());
        http.formLogin((login) -> login.disable());
        http.httpBasic((httpBasic) -> httpBasic.disable());
        http.oauth2Login((oauth2) -> oauth2
                .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.clientRegistrationRepository()))
                .userInfoEndpoint((userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService)))
                .successHandler(oAuth2SuccessHandler));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(antMatcher("/"), antMatcher("/open-api/**"), antMatcher("/login")).permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
