package org.example.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 因为WebSocket是类似客户端服务端的形式(采用ws协议)
 * <p>那么这里的WebSocketServer其实就相当于一个ws协议的Controller
 *
 * @author song2m
 * @since 2023/2/18 22:21
 */
@Component
@Service
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 记录在线连接数
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象
     */
    private static final CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String sid;

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);
        this.sid = sid;
        addOnlineCount();
        try {
            senMessage("connect success...");
            logger.info("有新窗口开始监听：{}，当前在线人数为：{}", sid, getOnlineCount());
        } catch (IOException e) {
            logger.error("websocket exception");
            throw new RuntimeException(e);
        }

    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        logger.info("释放的sid：{}", sid);
        logger.info("有一连接关闭！当前在线人数为：{}", getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到来自窗口：{} 的信息：{}", sid, message);

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


    /**
     * 发送信息
     */
    public void senMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}
