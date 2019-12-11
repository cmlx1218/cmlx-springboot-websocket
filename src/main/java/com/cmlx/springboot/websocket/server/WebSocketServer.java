package com.cmlx.springboot.websocket.server;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-11 0011 16:26
 */
@Slf4j
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {

    //静态变量，用来记录当前在线连接数，应该设计成线程安全额的
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    //与某个客户端的连接会话，需要通过它给客户端发送数据
    private Session session;
    //接收sid
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     * @param sid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this); //加入set中
        addOnlineCount();       //在线人数加1
        log.info("有新窗口开始监听：" + sid + ",当前在线人数为：" + getOnlineCount());
        this.sid = sid;
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();              //在线人数减1
        log.info("有一连接关闭！当前人数为：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口" + sid + "的信息：" + message);
        //群发消息
        for (WebSocketServer socketServer : webSocketSet) {
            try {
                socketServer.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    
    /**
     * 实现服务器主动推送
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     *
     * @param message
     * @param sid
     * @throws IOException
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + ",推送内容：" + message);
        for (WebSocketServer webSocketServer : webSocketSet) {
            try {
                if (sid == null) {
                    webSocketServer.sendMessage(message);
                } else if (webSocketServer.sid.equals(sid)) {
                    webSocketServer.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
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

}
