package org.elitclass.api.domain.webide;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Setter;
import org.elitclass.api.config.websocket.SpringContextBridge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    public void onMessage(Session session, String message) {
        try {
            String containerId = (String) session.getUserProperties().get("containerId");
            if (containerId == null) {
                send(session, "Error: containerId not found in session.");
                return;
            }

            final String msg = (message == null ? "" : message.trim());

            // ===== STOP ONLY =====
            if ("__STOP__".equalsIgnoreCase(msg) || msg.startsWith("STOP:")) {
                // 우선순위: STOP:projectName 형식이면 그걸 사용, 없으면 세션에 저장된 projectName 사용
                String pn = null;
                if (msg.startsWith("STOP:")) {
                    pn = msg.substring("STOP:".length()).trim();
                }
                if (pn == null || pn.isEmpty()) {
                    pn = (String) session.getUserProperties().get("projectName");
                }
                if (pn == null || pn.isEmpty()) {
                    send(session, "⚠️ STOP ignored: projectName is unknown for this session.");
                    return;
                }
                stopProcesses(containerId, pn, session);
                return;
            }

            // ===== NORMAL BUILD START =====
            final String projectName = msg;
            session.getUserProperties().put("projectName", projectName); // 이후 STOP 시 활용

            String cmd = String.format(
                    "cd /usr/src/%s && ./gradlew build --no-daemon && java -jar build/libs/*0.0.1-SNAPSHOT.jar",
                    projectName
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
                            System.out.print("[DOCKER LOG] " + output); // 서버 로그
                            send(session, output);                       // 프론트로 전달
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

    // ====== 추가: 컨테이너 안에서 Spring Boot/Gradle 프로세스 종료 ======
    private void stopProcesses(String containerId, String projectName, Session session) {
        try {
            // java -jar (projectName jar) 와 Gradle Daemon을 순하게 종료 후 필요 시 강제 종료
            String stopScript =
                    "set -e\n" +
                            "echo \"[STOP] Scanning processes...\"\n" +
                            // jar 실행 PID 탐색 (경로/이름에 projectName 포함된 java 커맨드)
                            "JAR_PIDS=$(ps -eo pid,command | awk '/java .*" + escapeSed(projectName) + ".*\\.jar/ {print $1}')\n" +
                            "if [ -n \"$JAR_PIDS\" ]; then echo \"[STOP] Sending SIGTERM to:  $JAR_PIDS\"; kill -TERM $JAR_PIDS || true; fi\n" +
                            // Gradle Daemon도 정리
                            "GRADLE_PIDS=$(ps -eo pid,command | awk '/GradleDaemon|org\\.gradle\\.launcher\\.daemon/ {print $1}')\n" +
                            "if [ -n \"$GRADLE_PIDS\" ]; then echo \"[STOP] Sending SIGTERM to Gradle daemons: $GRADLE_PIDS\"; kill -TERM $GRADLE_PIDS || true; fi\n" +
                            "sleep 1\n" +
                            // 아직 살아있으면 KILL
                            "JAR_PIDS2=$(ps -eo pid,command | awk '/java .*" + escapeSed(projectName) + ".*\\.jar/ {print $1}')\n" +
                            "if [ -n \"$JAR_PIDS2\" ]; then echo \"[KILL] Force killing: $JAR_PIDS2\"; kill -KILL $JAR_PIDS2 || true; fi\n" +
                            "GRADLE_PIDS2=$(ps -eo pid,command | awk '/GradleDaemon|org\\.gradle\\.launcher\\.daemon/ {print $1}')\n" +
                            "if [ -n \"$GRADLE_PIDS2\" ]; then echo \"[KILL] Force killing Gradle daemons: $GRADLE_PIDS2\"; kill -KILL $GRADLE_PIDS2 || true; fi\n" +
                            "echo \"[STOP] Done.\"";

            ExecCreateCmdResponse stopExec = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .withCmd("sh", "-c", stopScript)
                    .exec();

            dockerClient.execStartCmd(stopExec.getId())
                    .exec(new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            String output = new String(frame.getPayload(), StandardCharsets.UTF_8);
                            System.out.print(output);
                            send(session, output);
                            super.onNext(frame);
                        }
                    });

        } catch (Exception e) {
            send(session, "❌ STOP failed: " + e.getMessage());
        }
    }

    // 작은 유틸 (awk 정규식에서 안전하게 쓰도록 프로젝트명 이스케이프)
    private String escapeSed(String s) {
        return s.replace("\\", "\\\\").replace("/", "\\/").replace(".", "\\.");
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