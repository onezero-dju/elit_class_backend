package org.elitclass.api.domain.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.elitclass.api.domain.webide.model.*;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.api.exception.api.ApiException;
import org.elitclass.api.exception.docker.DockerOperationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

@Service
public class WebIdeService {

    private final DockerClient dockerClient;

    public WebIdeService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    //java 컨테이너 생성
    //생성후 redis에 정보 추가
    public WebIdeCreateResponse createIdeWithJDK(Long userId){
        try{

            String containerName = "webIde-"+userId+"-"+UUID.randomUUID().toString();
            String imageName = "java";
            //컨테이너 만들기

            CreateContainerResponse response = dockerClient.createContainerCmd("openjdk:22-ea-16-jdk")
                    .withName(containerName)
                    .withTty(true)
                    .withCmd("tail","-f","dev/null")
                    .exec();

            //컨테이너 가동
            dockerClient.startContainerCmd(response.getId()).exec();

            //컨테이너 관련 정보 넘기기
            return WebIdeCreateResponse.builder()
                    .containerId(response.getId())
                    .userId(userId)
                    .build();

        }catch (Exception e){
            throw new DockerOperationException("Error creating ide", e);
        }
    }

    //생성
    public WebIdeCreateResponse createIde(Long userId){
        int externalPort = new Random().ints(10000,11000).findFirst().orElseThrow();

        CreateContainerResponse container = dockerClient.createContainerCmd("openjdk-container")
                .withName("webide-"+userId)
                .withExposedPorts(new ExposedPort(8080))
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(new PortBinding(
                                Ports.Binding.bindPort(externalPort),
                                new ExposedPort(8080)))
                        .withAutoRemove(true)
                )
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        return WebIdeCreateResponse.builder()
                .containerId(container.getId())
                .userId(userId)
                .build();
    }

    //컨테이너 조회

    //컨테이너 삭제

    //컨테이너 삭제후 redis에서도 삭제
    public WebIdeDeleteResponse deleteIde(String containerId) {

        containerId = containerId.replaceAll("^\"|\"$", "").trim();

        System.out.println(containerId);

        try {
            dockerClient.inspectContainerCmd(containerId).exec();

            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .exec();
            WebIdeDeleteResponse response = WebIdeDeleteResponse.builder()
                    .containerId(containerId)
                    .message("Deleted ide")
                    .build();
            return response;

        } catch (NotFoundException e) {
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        }

    }

    //containerId로 컨테이너 가동
    public WebIdeRunContainerResponse runIdeWithContainerId(String containerId){
        containerId = containerId.replaceAll("^\"|\"$", "").trim();


        try{
            InspectContainerResponse info = dockerClient.inspectContainerCmd(containerId).exec();
            if(!Boolean.TRUE.equals(info.getState().getRunning())){
                dockerClient.startContainerCmd(containerId).exec();
            }
            WebIdeRunContainerResponse response = WebIdeRunContainerResponse.builder()
                    .containerId(containerId)
                    .containerName(info.getName())
                    .status(info.getState().getStatus())
                    .build();

            return response;
        }catch (NotFoundException e){
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        }catch(Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR,e);
        }


    }
    //유저에 대한 코드를 저장하는 로직
    public SaveCodeResponse saveCode(SaveCodeRequest request) {
        var containerId = request.getContainerId().replaceAll("^\"|\"$", "").trim();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        try {
            dockerClient.inspectContainerCmd(containerId).exec();
            // 코드 문자열을 base64로 인코딩
            String base64Code = Base64.getEncoder().encodeToString(
                    request.getCode().getBytes(StandardCharsets.UTF_8)
            );

            // base64로 저장: echo <code> | base64 -d > /usr/src/Main.java
            String[] saveSourceCommand = {
                    "sh", "-c",
                    "echo '" + base64Code + "' | base64 -d > /usr/src/Main.java"
            };

            // 명령 생성
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd(saveSourceCommand)
                    .exec();

            // 명령 실행 + 결과 출력
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, errorStream))
                    .awaitCompletion();

            // 결과 로그 출력 (디버깅용)
            System.out.println("STDOUT:\n" + outputStream);
            System.out.println("STDERR:\n" + errorStream);

            return SaveCodeResponse.builder()
                    .code(request.getCode())
                    .message("성공적으로 데이터를 저장했습니다")
                    .build();

        } catch (NotFoundException e) {
            throw new ApiException(ErrorCode.BAD_REQUEST, e);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.SERVER_ERROR, e);
        }
    }

    //TODO: return 응답 개발
    public WebIdeBuildResponse buildIde(String containerId){
        containerId = containerId.replaceAll("^\"|\"$", "").trim();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        try{
            String[] sourceCompilerCmd = {
                    "javac","/usr/src/Main.java",
            };
            String[] SourceBuildCmd = {"Java","/user/src/Main"};
            dockerClient.inspectContainerCmd(containerId).exec();
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd(sourceCompilerCmd)
                    .withCmd(SourceBuildCmd)
                    .exec();

            // 명령 실행 + 결과 출력
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, errorStream))
                    .awaitCompletion();

            return WebIdeBuildResponse.builder()
                    .containerId(containerId)
                    .error(errorStream.toString())
                    .output(outputStream.toString())
                    .build();
        }catch(NotFoundException e){
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        }catch(Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR,e);
        }
    }



}
