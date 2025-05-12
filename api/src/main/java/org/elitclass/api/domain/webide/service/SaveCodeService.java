package org.elitclass.api.domain.webide.service;

import com.github.dockerjava.api.DockerClient;
import org.elitclass.api.domain.webide.model.FileUploadRequest;
import org.elitclass.api.domain.webide.model.SaveCodeResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SaveCodeService {

    private final DockerClient dockerClient;

    public SaveCodeService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public  void saveFileTree(FileUploadRequest rootNode,String baseDir ) throws IOException {
        Path basePath = Paths.get(baseDir);
        saveRecursively(rootNode,basePath);
    }
    private void saveRecursively(FileUploadRequest node,Path currentPath) throws IOException {
        Path targetPath = currentPath.resolve(node.name());
        if("folder".equalsIgnoreCase(node.type())){
            Files.createDirectories(targetPath);
            if(node.children() != null){
                for(FileUploadRequest child : node.children()){
                    saveRecursively(child,targetPath);
                }
            }
        } else if ("file".equalsIgnoreCase(node.type())) {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath,node.content().getBytes(StandardCharsets.UTF_8));
        }
    }
}
