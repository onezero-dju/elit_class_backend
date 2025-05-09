package org.elitclass.api.config.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DockerConfig {
    @Bean
    public DockerClient dockerClient(){
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .withDockerTlsVerify(false)
                .withDockerCertPath(null)
                .build();
        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        return DockerClientImpl.getInstance(config, dockerHttpClient);
    }
    @Bean
    public HostConfig hostConfig(){
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("8080:80"));

        return hostConfig;
    }


}
