package org.elitclass.api.domain.webide;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Setter;
import org.elitclass.api.config.websocket.SpringContextBridge;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/terminal")
public class WebIdeTerminalWebSocket {

    @Setter
    private static DockerClient dockerClient;

    private static final ConcurrentHashMap<String, TerminalSession> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        this.dockerClient = SpringContextBridge.getBean(DockerClient.class);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        TerminalSession terminalSession = sessionMap.get(session.getId());


        if (terminalSession == null) {
            // 초기 메시지: containerId를 받는 경우
            String containerId = message;
            try {
                ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                        .withAttachStderr(true)
                        .withAttachStdout(true)
                        .withAttachStdin(true)
                        .withTty(true)
                        .withCmd("bash")
                        .exec();

                // 입출력 스트림 생성
                PipedOutputStream stdin = new PipedOutputStream();
                PipedInputStream stdinPipe = new PipedInputStream(stdin);

                // 출력 콜백 등록 및 stdin 연결
                ExecStartResultCallback callback = new ExecStartResultCallback() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            String output = new String(frame.getPayload(), StandardCharsets.UTF_8)
                                    .replaceAll("\\e\\[[\\d;]*[^\\d;]", "")
                                    .replaceAll("^[ \t]+", "");
                            session.getBasicRemote().sendText(output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        super.onNext(frame);
                    }
                };
                dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .withTty(true)
                        .withStdIn(stdinPipe)
                        .exec(callback);

                sessionMap.put(session.getId(), new TerminalSession(stdin));
                session.getBasicRemote().sendText("[Connected to container " + containerId + "]\n");

            } catch (Exception e) {
                try {
                    session.getBasicRemote().sendText("Error: " + e.getMessage());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            // 입력을 실행 중인 쉘로 전달
            try {
                terminalSession.getStdin().write(message.getBytes(StandardCharsets.UTF_8));
                terminalSession.getStdin().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        TerminalSession terminalSession = sessionMap.remove(session.getId());
        if (terminalSession != null) {
            try {
                terminalSession.getStdin().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private static class TerminalSession {
        private final OutputStream stdin;

        public TerminalSession(OutputStream stdin) {
            this.stdin = stdin;
        }

        public OutputStream getStdin() {
            return stdin;
        }
    }
}