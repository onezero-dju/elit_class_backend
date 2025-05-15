package org.elitclass.api.service;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.model.PageDto;
import org.elitclass.api.model.PageRequest;
import org.elitclass.db.page.PageEntity;
import org.elitclass.db.page.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final PageConverter pageConverter;

    // Create(페이지 생성)
    public PageDto pageCreate(PageRequest pageRequest) {

        PageEntity entity = PageEntity.builder()
                .isQuiz(pageRequest.getIsQuiz())
                .title(pageRequest.getTitle())
                .context(pageRequest.getContext())
                .build();

        PageEntity saved = pageRepository.save(entity);
        return pageConverter.toDto(saved);
    }

    // Read(단일)
    public PageDto pageView(Long id){
        var entity = pageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Page not found"));
        return pageConverter.toDto(entity);
    }

    // Read(전체)
    public List<PageDto> pageViewAll(){
        return pageRepository.findAll().stream()
                .map(pageConverter::toDto)
                .collect(Collectors.toList());
    }

    // PageUpdate
    public PageDto pageUpdate(Long id, PageRequest pageRequest) {
        var entity = pageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Page not found"));
        entity.setTitle(pageRequest.getTitle());
        entity.setContext(pageRequest.getContext());
        entity.setContext(pageRequest.getContext());
        return pageConverter.toDto(pageRepository.save(entity));
    }

    // PageDelete
    public void pageDelete(Long id) {
        if (!pageRepository.existsById(id)) {
            throw new IllegalArgumentException("Page not found");
        }
        pageRepository.deleteById(id);
    }

    // PageReport
//    public void pageReport(Long id){
//        var entity = pageRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Page not found"));
//        entity.setStatus(PageStatus.Report);
//        pageRepository.save(entity);
//    }
}
