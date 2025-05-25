package org.elitclass.api.config.oauth;

import org.elitclass.api.oauth2.CustomClientRegistrationRepo;
import org.elitclass.api.oauth2.CustomOAuth2AuthorizedClientService;
import org.elitclass.api.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;



    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomClientRegistrationRepo customClientRegistrationRepo, CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService, JdbcTemplate jdbcTemplate) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customClientRegistrationRepo = customClientRegistrationRepo;
        this.customOAuth2AuthorizedClientService = customOAuth2AuthorizedClientService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin((login) -> login.disable());
        http.httpBasic((httpBasic) -> httpBasic.disable());

        http.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/test/")
                .invalidateHttpSession(true)
        );

        http.oauth2Login((oauth2) -> oauth2
                .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.clientRegistrationRepository()))
                .userInfoEndpoint((userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(customOAuth2UserService))));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/open-api/**", "/login").permitAll()
                .anyRequest().authenticated());


        return http.build();

    }
    

}
