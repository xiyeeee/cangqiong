package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务（管理端来单提醒）
 * 连接地址：ws://主机:端口/ws/{sid} ，sid 一般传当前登录员工 id
 */
@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("WebSocket 客户端 {} 建立连接", sid);
        SESSION_MAP.put(sid, session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到客户端 {} 消息: {}", sid, message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("WebSocket 客户端 {} 断开连接", sid);
        SESSION_MAP.remove(sid);
    }

    /**
     * 群发消息（支付成功来单时 PC 端收到 "1" 会语音提示）
     */
    public void sendToAllClient(String message) {
        for (Session session : SESSION_MAP.values()) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("WebSocket 推送失败", e);
            }
        }
    }
}
