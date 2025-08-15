package org.elitclass.api.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.domain.lecture.model.LectureDto;
import org.elitclass.api.domain.lecture.model.LectureRequest;
import org.elitclass.db.lecture.LectureEntity;
import org.elitclass.db.lecture.LectureRepository;
import org.elitclass.db.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureConverter lectureConverter;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    // Create(강의 생성)
    public LectureDto createLecture(LectureRequest lectureRequest) {

        LectureEntity entity = LectureEntity.builder()
                .lectureTitle(lectureRequest.getLectureTitle())
                .context(lectureRequest.getContext())
                .build();

        LectureEntity saved = lectureRepository.save(entity);
        return lectureConverter.toDto(saved);
    }

    // Read(단일)
    public LectureDto lectureView(Long id) {
        var entity = lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Lecture not found"));
        return lectureConverter.toDto(entity);
    }

    // Read(전체)
    public List<LectureDto> lectureViewAll() {
        return lectureRepository.findAll().stream()
                .map(lectureConverter::toDto)
                .collect(Collectors.toList());
    }

    // LectureUpdate
    public LectureDto lectureUpdate(Long id, LectureRequest lectureRequest) {
        var entity = lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Lecture not found"));
        entity.setLectureTitle(lectureRequest.getLectureTitle());
        entity.setContext(lectureRequest.getContext());
        return lectureConverter.toDto(lectureRepository.save(entity));
    }

    // LectuerDelete
    public void lectureDelete(Long id) {
        if (!lectureRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Lecture not found");
        }
        lectureRepository.deleteById(id);
    }


    public List<LectureDto> findByClassId(Long classId) {
        return lectureRepository.findByClassIdOrderByOrderAsc(classId)
                .stream().map(this::toDto).toList();
    }

    // LectuerReport
//    public void letureReport(Long id){
//        var entity = lectureRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Lecture not found"));
//        entity.setStatus(LectureStatus.lectureReport);
//        lectureRepository.save(entity);
//    }
}
