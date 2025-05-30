package org.elitclass.api.domain.webide;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.elitclass.api.config.websocket.SpringContextBridge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.websocket.*;
import lombok.Setter;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/build/{containerId}")
public class WebIdeBuildLogWebSocket {

    @Setter
    private static DockerClient dockerClient;

    private static final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("containerId") String containerId) {
        dockerClient = SpringContextBridge.getBean(DockerClient.class);
        session.getUserProperties().put("containerId", containerId);
        send(session, "✅ [Connected] Waiting for projectName...");
    }

    @OnMessage
    public void onMessage(Session session, String projectName) {
        try {
            String containerId = (String) session.getUserProperties().get("containerId");

            if (containerId == null) {
                send(session, "Error: containerId not found in session.");
                return;
            }

            // 실행 명령 로그
            String cmd = String.format(
                    "cd /usr/src/%s && ./gradlew build --no-daemon && java -jar build/libs/*0.0.1-SNAPSHOT.jar",
                    projectName.trim()
            );

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

                            // 서버 로그에 출력
                            System.out.print("[DOCKER LOG] " + output);

                            // 프론트에 전달
                            send(session, output);
                            super.onNext(frame);
                        }
                    });

        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            send(session, "❌ Error: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        send(session, "🔌 Disconnected.");
        sessionMap.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        send(session, "❌ WebSocket error: " + error.getMessage());
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