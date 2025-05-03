package org.elitclass.api.domain.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.elitclass.api.domain.webide.model.AccessWebIdeRequest;
import org.elitclass.api.domain.webide.model.WebIdeCreateRequest;
import org.elitclass.api.exception.api.ApiException;
import org.elitclass.api.exception.docker.DockerOperationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class WebIdeService {

    private final DockerClient dockerClient;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public WebIdeService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String createIdeWithJDK(WebIdeCreateRequest request){
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        try{
            //컨테이너 만들기
            CreateContainerResponse response = dockerClient.createContainerCmd("openjdk:22-ea-16-jdk")
                    .withName(request.getUserId().toString())
                    .withTty(true)
                    .withCmd("tail","-f","dev/null")
                    .exec();

            //컨테이너 가동
            dockerClient.startContainerCmd(response.getId()).exec();

            return "container created";

        }catch (Exception e){
            throw new DockerOperationException("Error creating ide", e);
        }
    }



}
