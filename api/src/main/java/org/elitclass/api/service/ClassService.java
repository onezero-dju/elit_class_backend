package org.elitclass.api.service;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.model.ClassDto;
import org.elitclass.api.model.ClassRequest;
import org.elitclass.db.classes.ClassRepository;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.user.UserRepository;
import org.elitclass.db.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    
    private final ClassRepository classRepository;
    private final ClassConverter classConverter;
    private final UserRepository userRepository;

    // Create(클래스 생성)
    public ClassDto create(ClassRequest classRequest) {
        // 로그인 시스템 구현하면 인증된 사용자 정보로 대체
        Long userId = classRequest.getUserId();
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 직접 엔티티 생성
        ClassesEntity entity = ClassesEntity.builder()
            .classTitle(classRequest.getClassTitle())
            .description(classRequest.getDescription())
            .isPremium(false)
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
    public ClassDto update(Long id, ClassRequest request) {
        var entity = classRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
        entity.setClassTitle(request.getClassTitle());
        entity.setDescription(request.getDescription());
        return classConverter.toDto(classRepository.save(entity));
    }

    // Delete
    public void delete(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
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
} 