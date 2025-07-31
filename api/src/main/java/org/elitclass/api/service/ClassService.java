package org.elitclass.api.service;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.model.ClassDto;
import org.elitclass.api.model.ClassRequest;
import org.elitclass.db.classes.ClassRepository;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.user.UserRepository;
import org.elitclass.db.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    
    private final ClassRepository classRepository;
    private final ClassConverter classConverter;
    private final UserRepository userRepository;

    private UserEntity getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자 입니다. 로그인이 필요합니다.");
        }
        if(authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            String providerId = customUser.getProviderId();
            return userRepository.findByProviderId(providerId)
                    .orElseThrow( () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자" + providerId + "를 찾을 수 없습니다."));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }


    // Create(클래스 생성)
    public ClassDto create(ClassRequest classRequest, Authentication authentication) {
        //클래스 생성은 반드시 인증된 사용자만 가능
        UserEntity user = getAuthenticatedUser(authentication);

        ClassesEntity entity = ClassesEntity.builder()
                .classTitle(classRequest.getClassTitle())
                .description(classRequest.getDescription())
                .imageUrl(classRequest.getImageUrl())
                .likeCount(0L)
                .views(0L)
                .status(ClassStatus.REGISTERED)
                .user(user)
                .build();

        ClassesEntity saved = classRepository.save(entity);
        return classConverter.toDto(saved);
    }

    // Read (단일)
    public ClassDto view(Long id) {
        var entity = classRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
        return classConverter.toDto(entity);
    }

    // Read (전체)
    public List<ClassDto> viewAll() {
        return classRepository.findAll().stream()
                .map(classConverter::toDto)
                .collect(Collectors.toList());
    }

    // Update
    public ClassDto update(Long id, ClassRequest request, Authentication authentication) {
        // 현재 로그인된 사용자 정보 가져오기
        UserEntity currentUser = getAuthenticatedUser(authentication);

        //수정하려는 클래스 엔티티 조회
        ClassesEntity entity = classRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        // 클래스 소유자 확인 로직
        // 클래스를 생성한 사용자의 providerId와 현재 로그인된 사용자의 providerId가 다른 경우
        if (!Objects.equals(entity.getUser().getProviderId(), currentUser.getProviderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 클래스를 수정할 권한이 없습니다.");
        }

        entity.setClassTitle(request.getClassTitle());
        entity.setDescription(request.getDescription());
        return classConverter.toDto(classRepository.save(entity));
    }

    // Delete
    public void delete(Long id, Authentication authentication) {
        // 현재 로그인된 사용자 정보 가져오기
        UserEntity currentUser = getAuthenticatedUser(authentication);

        ClassesEntity entity = classRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        if (!Objects.equals(entity.getUser().getProviderId(), currentUser.getProviderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 클래스를 삭제할 권한이 없습니다.");
        }
        classRepository.deleteById(id);
    }

    // Report
    public void report(Long id) {
        var entity = classRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
        entity.setStatus(ClassStatus.Report);
        classRepository.save(entity);
    }

    public List<ClassDto> getPopularClasses() {
        List<ClassesEntity> entities = classRepository.findTop5ByOrderByLikesDesc();
        return entities.stream()
                .map(classConverter::toDto)
                .collect(Collectors.toList());
    }
} 