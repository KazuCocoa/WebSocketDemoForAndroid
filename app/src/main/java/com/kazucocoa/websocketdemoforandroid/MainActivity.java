package com.kazucocoa.websocketdemoforandroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;

import java.io.EOFException;
import java.io.IOException;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class MainActivity extends AppCompatActivity {

    @Inject
    WebSocketClient socketClient;

    private Channel channel;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Scope scope = Toothpick.openScopes(getApplication(), WebSocketClient.class, this);
        scope.installModules(new SmoothieActivityModule(this));
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, scope);

        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.activity_main_text);

        try {
            channel = socketClient.openChannel("my_room:lobby");
            joinRoom(channel);
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

                    channel.on("new_message", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            setText("NEW MESSAGE: " + envelope.toString());
                        }
                    });

                    channel.onClose(new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            setText("CLOSED: " + envelope.toString());
                        }
                    });

                    channel.onError(new IErrorCallback() {
                        @Override
                        public void onError(String reason) {
                            setText("ERROR: " + reason);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void joinRoom(Channel channel) throws IOException {
        channel.join()
                .receive("ignore", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        setText("IGNORE joining the room");
                    }
                })
                .receive("ok", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        setText("JOINED with " + envelope.toString());
                    }
                });

    }

    private void setText(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(data);
            }
        });
        }
}
