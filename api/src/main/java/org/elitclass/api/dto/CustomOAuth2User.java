package org.elitclass.api.dto;

import org.elitclass.db.user.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    // UserEntity를 직접 포함하도록 수정
    private final UserEntity userEntity;
    private final Map<String, Object> attributes;

    // 역할(Role)도 UserEntity에서 직접 가져올 수 있습니다.

    public CustomOAuth2User(UserEntity userEntity, Map<String, Object> attributes) {
        this.userEntity = userEntity;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(userEntity.getRole()); // UserEntity에서 역할 가져오기
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return userEntity.getProviderId(); // providerId를 고유 식별자로 사용
    }

    // OAuth2SuccessHandler에서 사용하기 위한 메서드
    public UserEntity getUserEntity() {
        return this.userEntity;
    }

    public String getEmail() {
        return userEntity.getEmail();
    }
}
