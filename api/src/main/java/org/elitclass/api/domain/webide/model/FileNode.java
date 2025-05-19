package org.elitclass.api.domain.webide.model;

import java.util.List;

public record FileNode(
        String name,
        String type,
        String content,
        List<FileNode> children
){}
