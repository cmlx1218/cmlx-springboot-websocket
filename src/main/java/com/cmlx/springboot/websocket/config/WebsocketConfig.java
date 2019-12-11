package com.cmlx.springboot.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Desc
 * @Author cmlx
 * @Date 2019-12-11 0011 16:05
 */
@Configuration
public class WebsocketConfig {

    /**
     * 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocketendpoint
     * 如果使用独立的servlet容器，而不是直接使用springboot内置容器，就不用注入ServerEndpointExporter，因为他将由ring器自己提供和管理
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
