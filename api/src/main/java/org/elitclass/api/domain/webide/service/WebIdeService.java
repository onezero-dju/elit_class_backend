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
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.elitclass.api.domain.webide.model.*;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.api.exception.api.ApiException;
import org.elitclass.api.exception.docker.DockerOperationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    //TODO: 생성후 redis에 정보 추가
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
    //TODO : 포트중복 최소화
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

    //TODO: 컨테이너 조회



    //컨테이너 삭제
    //컨테이너 삭제후 redis에서도 삭제
    public WebIdeDeleteResponse deleteIde(String containerId) {

        containerId = containerId.replaceAll("^\"|\"$", "").trim();


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
    //TODO: 코드 삭제
    public void saveFileTreeToContainer(FileUploadRequest request) throws IOException {

        UUID uuid = UUID.randomUUID();
        String projectName = uuid+"-"+request.projectName();
        Path projectPath = Paths.get("./tmp",projectName);
        Files.createDirectories(projectPath);
        saveRecursively(request.files() ,projectPath);

        Path tarPath = Paths.get("./tmp",projectName+".tar");
        File tarFile =tarPath.toFile();

        System.out.println(tarFile.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(tarFile);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(fos)){

            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            Files.walk(projectPath).filter(path->!Files.isDirectory(path)).forEach(path -> {
                try{
                    Path relativePath = projectPath.getParent().relativize(path);
                    TarArchiveEntry entry = new TarArchiveEntry(path.toFile(),relativePath.toString());
                    taos.putArchiveEntry(entry);
                    Files.copy(path, taos);
                    taos.closeArchiveEntry();
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            taos.finish();
        }

        try{
            System.out.println(request.containerId());
            dockerClient.copyArchiveToContainerCmd(request.containerId())
                    .withHostResource(tarFile.getAbsolutePath())
                    .withRemotePath("/usr/src")
                    .exec();

            System.out.println(tarFile.getName());

            var execCreateCmdResponse = dockerClient.execCreateCmd(request.containerId())
                    .withCmd("tar", "-xvf", "/usr/src/"+tarFile.getName(), "-C", "/usr/src")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(System.out, System.err))
                    .awaitCompletion();

            Files.delete(tarFile.toPath());
        }catch (NotFoundException | NullPointerException e){
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 파일 다운로드 함수
    private void saveRecursively(FileNode node, Path currentPath) throws IOException {
        Path targetPath = currentPath.resolve(node.name());
        if("folder".equalsIgnoreCase(node.type())){
            Files.createDirectories(targetPath);
            if(node.children() != null){
                for(FileNode child : node.children()){
                    saveRecursively(child,targetPath);
                }
            }
        } else if ("file".equalsIgnoreCase(node.type())) {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath,node.content().getBytes(StandardCharsets.UTF_8));
        }
    }


    //코드 실행 로직
    //TODO 파일 전체를 빌드하는 로직으로 리펙토링
    public WebIdeBuildResponse buildIde(String containerId){
        containerId = containerId.replaceAll("^\"|\"$", "").trim();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        try{
            String[] sourceCompilerCmd = {
                    "javac","/usr/src/Main.java",
            };
            String[] sourceBuildCmd = {"java","-cp","/usr/src/","Main"};
            dockerClient.inspectContainerCmd(containerId).exec();

            ExecCreateCmdResponse sourceCompileResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd(sourceCompilerCmd)
                    .exec();

            // 명령 실행 + 결과 출력
            dockerClient.execStartCmd(sourceCompileResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, errorStream))
                    .awaitCompletion();

            ExecCreateCmdResponse sourceBuildResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStderr(true)
                    .withAttachStderr(true)
                    .withCmd(sourceBuildCmd)
                    .exec();

            dockerClient.execStartCmd(sourceBuildResponse.getId())
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
