package org.elitclass.api.domain.webide.service;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.elitclass.api.domain.webide.model.*;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.api.exception.api.ApiException;
import org.elitclass.api.exception.docker.DockerOperationException;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
import org.elitclass.db.usercontainer.UserContainerEntity;
import org.elitclass.db.usercontainer.UserContainerRepository;
import org.elitclass.db.usercontainer.enums.Language;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebIdeService {

    private final DockerClient dockerClient;
    private final UserContainerRepository userContainerRepository;
    private final UserRepository userRepository;


    //TODO: 생성후 h2 데이터베이스에 추가
    public UserContainerEntity createIdeWithJDK(CreateIdeWithJdkRequest request){
        try{

            UserEntity userId = userRepository.findById(request.getUserId()).orElseThrow(
                    ()-> new ApiException(ErrorCode.BAD_REQUEST,"User not found")
            );

            String containerName = "webIde-"+request.getUserId()+"-"+UUID.randomUUID();
            //컨테이너 만들기

            CreateContainerResponse response = dockerClient.createContainerCmd("springboot-java17:latest")
                    .withName(containerName)
                    .withTty(true)
                    .withCmd("tail","-f","/dev/null")
                    .exec();

            //컨테이너 가동
            dockerClient.startContainerCmd(response.getId()).exec();

            var containerId = dockerClient.inspectContainerCmd(response.getId()).exec().getId();

            var data =UserContainerEntity.builder()
                    .userId(userId)
                    .containerName(containerName)
                    .projectName(request.getProjectName())
                    .language(Language.JAVA)
                    .containerId(containerId)
                    .build();

            //컨테이너 관련 정보 넘기기
            userContainerRepository.save(data);


            return data;

        }catch (Exception e){
            log.error("Error creating ide",e);
            throw new DockerOperationException("Error creating ide", e);
        }
    }

    //생성 업그레이드 함수 나중에는 이거 사용해야함
    //TODO : 포트중복 최소화
    public WebIdeCreateResponse createIde(Long userId){
        int externalPort = new Random().ints(10000,11000).findFirst().orElseThrow();

        CreateContainerResponse container = dockerClient.createContainerCmd("springboot-java17:latest")
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
     public List<GetWebIdeResponse> getWebIde(GetWebIdeRequest request){
        UserEntity user = userRepository.findById(request.getUserId()).orElseThrow(
                ()-> new ApiException(ErrorCode.BAD_REQUEST,"User not found")
        );
        List<UserContainerEntity> containers = userContainerRepository.findAllByUserIdAndLanguage(user,request.getLanguage());

         return containers.stream()
                 .map(container->GetWebIdeResponse.builder()
                         .containerName(container.getContainerName())
                         .projectName(container.getProjectName())
                         .createdAt(container.getCreatedAt())
                         .updatedAt(container.getUpdatedAt())
                         .build()
         ).toList();
    }



    //컨테이너 삭제
    public WebIdeDeleteResponse deleteIde(DeleteIdeRequest request) {

        UserEntity user =userRepository.findById(request.getUserId()).orElseThrow(
                ()-> new ApiException(ErrorCode.BAD_REQUEST,"User not found")
        );
        var container =userContainerRepository.findByUserIdAndLanguage(user ,request.getLanguage()).orElseThrow(
                ()-> new ApiException(ErrorCode.BAD_REQUEST,"No container found for userId")
        );

        try {

            dockerClient.inspectContainerCmd(container.getContainerId()).exec();

            dockerClient.removeContainerCmd(container.getContainerId())
                    .withForce(true)
                    .exec();

            WebIdeDeleteResponse response = WebIdeDeleteResponse.builder()
                    .containerId(container.getContainerId())
                    .message("Deleted ide")
                    .build();

            userContainerRepository.delete(container);

            return response;

        } catch (NotFoundException e) {
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        }

    }

    //containerId로 컨테이너 가동
    //TODO: UserId로 컨테인너 아이디를 찾은 후 컨테이너 아이디를 맵핑
    public WebIdeRunContainerResponse runIdeWithContainerId(String containerId){
        containerId = containerId.replaceAll("^\"|\"$", "").trim();


        try{
            InspectContainerResponse info = dockerClient.inspectContainerCmd(containerId).exec();
            if(!Boolean.TRUE.equals(info.getState().getRunning())){
                dockerClient.startContainerCmd(containerId).exec();
            }

            return WebIdeRunContainerResponse.builder()
                    .containerId(containerId)
                    .containerName(info.getName())
                    .status(info.getState().getStatus())
                    .build();

        }catch (NotFoundException e){
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        }catch(Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR,e);
        }
    }

    public WebIdeBuildResponse saveFileTreeToContainer(FileUploadRequest request) throws IOException {
        UUID uuid = UUID.randomUUID();
        String projectName = uuid + "-" + request.projectName();
        Path projectPath = Paths.get("./tmp", projectName);
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        Files.createDirectories(projectPath);
        for (FileNode file : request.files()) {
            saveRecursively(file, projectPath);
        }
        injectGradleWrapper(projectPath);

        Path tarPath = Paths.get("./tmp", projectName + ".tar");
        File tarFile = tarPath.toFile();

        try (FileOutputStream fos = new FileOutputStream(tarFile);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(fos)) {

            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            Files.walk(projectPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            Path relativePath = projectPath.getParent().relativize(path);
                            TarArchiveEntry entry = new TarArchiveEntry(path.toFile(), relativePath.toString());
                            taos.putArchiveEntry(entry);
                            Files.copy(path, taos);
                            taos.closeArchiveEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            taos.finish();
        }

        try {
            // 1. 압축 파일 컨테이너에 복사
            dockerClient.copyArchiveToContainerCmd(request.containerId())
                    .withHostResource(tarFile.getAbsolutePath())
                    .withRemotePath("/usr/src/")
                    .exec();

            // 2. 압축 해제
            String extractCmdId = dockerClient.execCreateCmd(request.containerId())
                    .withCmd("tar", "-xvf", "/usr/src/" + tarFile.getName(), "-C", "/usr/src")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec()
                    .getId();

            dockerClient.execStartCmd(extractCmdId)
                    .exec(new ExecStartResultCallback(System.out, System.err))
                    .awaitCompletion();

            // 3. 실행 권한 부여
            String chmodCmdId = dockerClient.execCreateCmd(request.containerId())
                    .withCmd("chmod", "+x", "/usr/src/" + projectName + "/gradlew")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec()
                    .getId();

            dockerClient.execStartCmd(chmodCmdId)
                    .exec(new ExecStartResultCallback(stdout, stderr))
                    .awaitCompletion();

            // 4. Gradle Build
            String buildCmdId = dockerClient.execCreateCmd(request.containerId())
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd("bash", "-c", "cd /usr/src/" + projectName + " && ./gradlew build --no-daemon")
                    .exec()
                    .getId();

            dockerClient.execStartCmd(buildCmdId)
                    .exec(new ExecStartResultCallback(stdout, stderr))
                    .awaitCompletion();

            // 5. Jar 실행
            String runJarCmdId = dockerClient.execCreateCmd(request.containerId())
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd("bash", "-c", "java -jar /usr/src/" + projectName + "/build/libs/*0.0.1-SNAPSHOT.jar")
                    .exec()
                    .getId();

            dockerClient.execStartCmd(runJarCmdId)
                    .exec(new ExecStartResultCallback(stdout, stderr))
                    .awaitCompletion();

            // 6. 정리
            Files.delete(tarFile.toPath());
            deleteDirectoryRecursively(projectPath);
            
            return WebIdeBuildResponse.builder()
                    .containerId(request.containerId())
                    .error(stderr.toString())
                    .output(stdout.toString())
                    .build();


        } catch (NotFoundException | NullPointerException e) {
            throw new ApiException(ErrorCode.BAD_REQUEST, e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    //재귀적으로 파일을 지우는 함수
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
                .sorted(Comparator.reverseOrder()) // 하위 파일 먼저 삭제
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("삭제 실패: " + p + " -> " + e.getMessage());
                    }
                });
    }
    // 파일 생성 함수
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

    //gradlew 강제 삽입
    private void injectGradleWrapper(Path projectRoot) throws IOException {
        Path wrapperDir = projectRoot.resolve("gradle/wrapper");

        Path gradlew = projectRoot.resolve("gradlew");
        Path wrapperJar = wrapperDir.resolve("gradle-wrapper.jar");
        Path wrapperProps = wrapperDir.resolve("gradle-wrapper.properties");

        ClassLoader classLoader = getClass().getClassLoader();

        if (!Files.exists(gradlew)) {
            try (InputStream is = classLoader.getResourceAsStream("gradle-wrapper-template/gradlew")) {
                if (is != null) {
                    Files.copy(is, gradlew, StandardCopyOption.REPLACE_EXISTING);
                    gradlew.toFile().setExecutable(true);
                }
            }
        }

        if (!Files.exists(wrapperJar)) {
            Files.createDirectories(wrapperDir);
            try (InputStream is = classLoader.getResourceAsStream("gradle-wrapper-template/gradle/wrapper/gradle-wrapper.jar")) {
                if (is != null) {
                    Files.copy(is, wrapperJar, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        if (!Files.exists(wrapperProps)) {
            try (InputStream is = classLoader.getResourceAsStream("gradle-wrapper-template/gradle/wrapper/gradle-wrapper.properties")) {
                if (is != null) {
                    Files.copy(is, wrapperProps, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
