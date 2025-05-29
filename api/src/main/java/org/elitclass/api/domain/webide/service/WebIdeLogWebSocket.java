package org.elitclass.api.domain.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.elitclass.api.config.websocket.SpringContextBridge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ServerEndpoint("/ws/logs")
public class WebIdeLogWebSocket {

    private DockerClient dockerClient;

    @OnOpen
    public void onOpen(Session session) {
        this.dockerClient = SpringContextBridge.getBean(DockerClient.class);
        send(session, "✅ [Connected] Ready to receive containerId and projectName...");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            String[] parts = message.split(",", 2);
            if (parts.length < 2) {
                send(session, "❌ Invalid format. Use: containerId,projectName");
                return;
            }

            String containerId = parts[0].trim();
            String projectName = parts[1].trim();

            String cmd = String.format("cd /usr/src/%s && ./gradlew build --no-daemon && java -jar build/libs/*0.0.1-SNAPSHOT.jar", projectName);

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .withCmd("bash", "-c", cmd)
                    .exec();

            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            String output = new String(frame.getPayload(), StandardCharsets.UTF_8);
                            send(session, output);
                            super.onNext(frame);
                        }
                    });

        } catch (Exception e) {
            send(session, "❌ Error: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        send(session, "🔌 [Disconnected]");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        send(session, "❌ Error: " + error.getMessage());
        error.printStackTrace();
    }

    private void send(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}