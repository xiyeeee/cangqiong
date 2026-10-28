package com.sky.task;

import com.sky.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket 定时任务（课程 Demo 用）
 * 来单提醒请走 {@link com.sky.service.OrderService#paySuccess} 推送，不要长期开启下面定时任务。
 */
@Component
public class WebSocketTask {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 测试 WebSocket 时可取消注释；正式联调 PC 来单请关闭，避免刷屏
     */
    // @Scheduled(cron = "0/5 * * * * ?")
    // public void sendMessageToClient() {
    //     webSocketServer.sendToAllClient("这是来自服务端的消息："
    //             + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    // }
}
