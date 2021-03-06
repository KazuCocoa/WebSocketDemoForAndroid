package com.kazucocoa.websocketdemoforandroid;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Socket;

import java.io.IOException;

import javax.inject.Singleton;

@Singleton
public class WebSocketClient {
    private String HOST = new WebSocketModel().HOST;

    public Socket socket;

    public WebSocketClient() {
        try {
            this.socket = new Socket("ws://" + HOST + "/socket/websocket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel openChannel(String room) {
        return socket.chan(room, null);
    }
}
