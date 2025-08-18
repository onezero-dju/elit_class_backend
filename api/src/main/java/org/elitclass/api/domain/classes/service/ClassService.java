package org.elitclass.api.domain.classes.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.domain.classes.model.ClassDto;
import org.elitclass.api.domain.classes.model.ClassRequest;
import org.elitclass.api.domain.classes.model.converter.ClassConverter;

import org.elitclass.db.classes.ClassRepository;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.user.UserRepository;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.enums.UserRole;
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

        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String providerId = principal.getProviderId();
        return userRepository.findByProviderId(providerId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자" + providerId + "를 찾을 수 없습니다."));
    }

    // Create(클래스 생성)
    public ClassDto create(ClassRequest classRequest, Authentication authentication) {
        //클래스 생성은 반드시 인증된 사용자만 가능
        UserEntity user = getAuthenticatedUser(authentication);

        ClassesEntity entity = ClassesEntity.builder()
                .classTitle(classRequest.getClassTitle())
                .description(classRequest.getDescription())
                .likeCount(0L)
                .views(0L)
                .imageUrl(classRequest.getImageUrl())
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

    public List<ClassDto> popular(){
        List<ClassesEntity> list =
                classRepository.findTop5ByStatusOrderByLikeCountDescIdDesc(
                        ClassStatus.REGISTERED);
        return list.stream().map(classConverter::toDto).toList();
    }

    // 내가 만든 클래스 조회
    public List<ClassDto> viewMine(Authentication authentication) {
        UserEntity user = getAuthenticatedUser(authentication);

        // 상태 필터를 원하면 아래 라인처럼 메서드 변경: findAllByUserAndStatusOrderByIdDesc(me, ClassStatus.REGISTERED)
        List<ClassesEntity> list = classRepository.findAllByUserId(user.getId());

        return list.stream()
                .map(classConverter::toDto)
                .collect(Collectors.toList());
    }

    // 👇 클래스 조회 + isOwner 계산
    public boolean isOwner(Authentication authentication, Long classId) {
        // 1) 현재 사용자 ID
        UserEntity user = getAuthenticatedUser(authentication); // ← 이미 있는 헬퍼 사용
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        Long userId = user.getId();

        // 2) 클래스 존재 여부 (404 분리)
        if (!classRepository.existsById(classId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        }

        // 3) 소유자 여부 (가장 빠른 쿼리)
        return classRepository.existsByIdAndUserId(classId, userId);
        // 연관관계(@ManyToOne user)라면 ↓ 로 변경
        // return classRepository.existsByIdAndUser_Id(classId, userId);
    }

    public List<ClassDto> adminList() {
        var list = classRepository
                .findTop3ByUser_RoleAndStatusOrderByLikeCountDescIdDesc(
                        UserRole.ADMIN, ClassStatus.REGISTERED);
        return list.stream().map(classConverter::toDto).toList();
    }

}

