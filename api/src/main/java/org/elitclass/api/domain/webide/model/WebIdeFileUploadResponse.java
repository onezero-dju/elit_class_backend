package org.elitclass.api.domain.webide.model;

import java.util.List;
public class WebIdeFileUploadResponse {

    private String name;
    private String type;
    private String content;
    private List<WebIdeFileUploadResponse> children;
}
