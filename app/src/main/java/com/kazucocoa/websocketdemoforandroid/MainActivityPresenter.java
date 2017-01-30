package com.kazucocoa.websocketdemoforandroid;

import android.widget.TextView;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketCloseCallback;

import java.io.IOException;

public class MainActivityPresenter implements Presenter {

    private MainActivityView mainActivityView;

    public MainActivityPresenter(MainActivityView view) {
        this.mainActivityView = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    public Channel establishConnection(WebSocketClient client, String room, final TextView view) throws IOException {
        client.socket
            .onClose(new ISocketCloseCallback() {
                @Override
                public void onClose() {
                    mainActivityView.setText(view, "CLOSED");
                }})
            .onError(new IErrorCallback() {
                @Override
                public void onError(String reason) {
                    mainActivityView.setText(view, "ERROR: " + reason);
                }})
            .connect();
        return client.openChannel(room);
    }

    public void joinRoom(Channel channel, final TextView view) throws IOException {
        channel.join()
                .receive("ignore", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        mainActivityView.setText(view, "IGNORE joining the room");
                    }
                })
                .receive("ok", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        mainActivityView.setText(view, "JOINED with " + envelope.toString());
                    }
                });
    }

    public void push(Channel channel, String message, ObjectNode node) throws IOException {
        channel.push(message, node);
    }

    public void on(Channel channel, String message, final TextView chatText) {
        channel.on(message, new IMessageCallback() {
            @Override
            public void onMessage(Envelope envelope) {
                mainActivityView.addText(chatText, "name: " + envelope.getPayload().findValue("name").toString());
                mainActivityView.addText(chatText, "message: " + envelope.getPayload().findValue("message").toString());
                mainActivityView.addText(chatText, "\n");
            }
        });
    }
}
