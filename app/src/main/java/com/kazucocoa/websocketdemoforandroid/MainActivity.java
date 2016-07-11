package com.kazucocoa.websocketdemoforandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.Socket;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String HOST = "localhost:4000";

    private Socket socket;

    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            channel = setUpSocket("my_room:lobby");

            channel.join()
                    .receive("ignore", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            System.out.println("IGNORE");
                        }
                    })
                    .receive("ok", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            System.out.println("JOINED with " + envelope.toString());
                        }
                    });

            channel.on("new:msg", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    System.out.println("NEW MESSAGE: " + envelope.toString());
                }
            });

            channel.onClose(new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    System.out.println("CLOSED: " + envelope.toString());
                }
            });

            channel.onError(new IErrorCallback() {
                @Override
                public void onError(String reason) {
                    System.out.println("ERROR: " + reason);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.send_message_button);
        assert button != null;
        button.setOnClickListener(sendMessageToPhoenix());
    }

    private View.OnClickListener sendMessageToPhoenix() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                        .put("name", "kazu android")
                        .put("message", "hello");
                try {
                    channel.push("new_message", node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Channel setUpSocket(String room) throws IOException {
        socket = new Socket("ws://" + HOST + "/socket/websocket");
        socket.connect();
        return socket.chan(room, null);
    }
}
