package com.kazucocoa.websocketdemoforandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IErrorCallback;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ISocketCloseCallback;

import java.io.IOException;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    @Inject
    WebSocketClient socketClient;

    private Channel channel;

    private TextView textView;

    private TextView chatText;

    private EditText editText;

    MainActivityModel mainActivityModel = new MainActivityModel();

    MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Scope scope = Toothpick.openScopes(getApplication(), WebSocketClient.class, this);
        scope.installModules(new SmoothieActivityModule(this));
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, scope);

        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.activity_main_text);
        chatText = (TextView) findViewById(R.id.chatText);
        editText = (EditText) findViewById(R.id.editText);

        try {
            channel = mainActivityPresenter.establishConnection(socketClient, mainActivityModel.room, textView);
            mainActivityPresenter.joinRoom(channel, textView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.send_message_button);
        assert button != null;
        button.setOnClickListener(sendMessageToPhoenixOnClickListener());

        Button clearButton = (Button) findViewById(R.id.clearButton);
        assert clearButton != null;
        clearButton.setOnClickListener(clearButtonOnClickListener());

    }

    private View.OnClickListener clearButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(chatText, "");
            }
        };
    }

    private View.OnClickListener sendMessageToPhoenixOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                        .put("name", mainActivityModel.name)
                        .put("message", String.valueOf(editText.getText()));
                try {
                    mainActivityPresenter.push(channel, "new_message", node);
                    mainActivityPresenter.on(channel, "new_message", chatText);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void setText(final TextView view, final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(data);
            }
        });
    }

    @Override
    public void addText(final TextView view, final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.append(data);
            }
        });
    }
}
