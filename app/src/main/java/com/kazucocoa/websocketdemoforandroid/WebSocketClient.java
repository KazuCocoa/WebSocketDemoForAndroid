package com.kazucocoa.websocketdemoforandroid;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Socket;

import java.io.IOException;

import javax.inject.Singleton;

@Singleton
public class WebSocketClient {
    private String HOST = "localhost:4000";

    public Socket socket;

    public WebSocketClient() {
        try {
            this.socket = new Socket("ws://" + HOST + "/socket/websocket");
            this.socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel openChannel(String room) {
        return socket.chan(room, null);
    }
}
