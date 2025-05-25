package org.elitclass.api.service;

import io.jsonwebtoken.lang.Classes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.model.LikesViewDto;
import org.elitclass.db.classes.ClassRepository;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.likes.LikesEntity;
import org.elitclass.db.likes.LikesRepository;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LikesViewService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;

//    public int getLikeClass(Long classId){
//        ClassesEntity classes = classRepository.findById(classId)
//                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
//        List<LikesEntity> num = likesRepository.findByClasses(classes);
//        return num.size();
//    }

    @Transactional
    public LikesViewDto pressLike(Long classId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ClassesEntity classes = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        LikesViewDto likesViewDto = new LikesViewDto();

        if (user.getLikes().stream().anyMatch(like -> like.getClasses().equals(classes))){
            likesRepository.deleteByUserAndClasses(user, classes);
            likesViewDto.setLikeCheck(0);
        }else{
            likesRepository.save(LikesEntity.builder().user(user).classes(classes).build());
            likesViewDto.setLikeCheck(1);
        }
//        List<LikesEntity> num = likesRepository.findByClasses(classes);
//
//        likesViewDto.setCount(num.size());
//        likesViewDto.setUserName(String.valueOf(user.getId()));
        return likesViewDto;
    }
}
