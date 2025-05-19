package org.elitclass.api.domain.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.elitclass.api.domain.webide.model.FileNode;
import org.elitclass.api.domain.webide.model.FileUploadRequest;
import org.elitclass.api.domain.webide.model.SaveCodeResponse;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.api.exception.api.ApiException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class SaveCodeService {

    private final DockerClient dockerClient;

    public SaveCodeService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    //TODO: response 구현, userId+uuid를 사용해서 파일명 명시
    public void saveFileTreeToContainer(FileUploadRequest request) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        UUID uuid = UUID.randomUUID();
        String projectName = uuid+"-"+request.projectName();
        System.out.println("Saving file tree to container " + request.projectName());
        System.out.println("Saving file tree to container " + request.containerId());
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
                    .exec(new ExecStartResultCallback(outputStream, errorStream))
                    .awaitCompletion();
            var inspect = dockerClient.inspectExecCmd(execCreateCmdResponse.getId()).exec();
            int exitCode = inspect.getExitCode();
            System.out.println("Exit Code: " + exitCode);
            System.out.println("STDOUT:\n" + outputStream);
            System.out.println("STDERR:\n" + errorStream);
        }catch (NotFoundException | NullPointerException e){
            throw new ApiException(ErrorCode.BAD_REQUEST,e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
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


}
