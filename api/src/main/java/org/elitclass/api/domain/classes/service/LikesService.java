package org.elitclass.api.domain.classes.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.domain.classes.model.LikesDto;
import org.elitclass.db.classes.ClassRepository;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.likes.LikesEntity;
import org.elitclass.db.likes.LikesRepository;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private final LikesRepository likesRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikesDto addLike(long userId, long classId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ClassesEntity classes = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        LikesDto likesDto = new LikesDto();

        // 좋아요 중복 확인을 위한 메서드 사용
        if(likesRepository.existsByUserAndClasses(user, classes)) {
            // 이미 좋아요를 눌렸다면 취소
            likesRepository.deleteByUserAndClasses(user, classes);
            classes.setLikeCount(classes.getLikeCount() - 1);
            classRepository.save(classes);

            likesDto.setMessage("좋아요 취소");
        }else{
            likesRepository.save(LikesEntity.builder()
                    .classes(classes)
                    .user(user)
                    .build()
            );
            classes.setLikeCount(classes.getLikeCount() + 1);
            classRepository.save(classes);

            likesDto.setMessage("좋아요 성공");
        }
        likesDto.setLikeCount(likesDto.getLikeCount());
        return likesDto;
    }




    public int selectLike(Long classId){
        ClassesEntity classes = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        LikesDto likesDto = new LikesDto();
        int count = classes.getLikes().size();
        return count;
    }
}
