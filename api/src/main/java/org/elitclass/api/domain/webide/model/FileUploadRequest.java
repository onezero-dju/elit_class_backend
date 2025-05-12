package org.elitclass.api.domain.webide.model;

import java.util.List;

public record FileUploadRequest (
    String containerId,
    String name,
    String type,
    String content,
    List<FileUploadRequest> children
){}
