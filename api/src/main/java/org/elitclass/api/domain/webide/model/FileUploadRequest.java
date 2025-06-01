package org.elitclass.api.domain.webide.model;

import org.elitclass.db.usercontainer.enums.Language;

import java.util.List;

public record FileUploadRequest (
    Long userId,
    Language language,
    List<FileNode> files
){}


