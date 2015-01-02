package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.chesshero.R;
import com.chesshero.client.ChessHeroApplication;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;
import com.kt.game.Color;


public class MainActivity extends Activity implements EventCenterObserver {

    private Intent pageToOpen;

    private Client client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        client = ((ChessHeroApplication) getApplication()).getClient();
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
    }

    public void openRegisterPage(View view) {
        pageToOpen = new Intent(this, RegisterActivity.class);
        startActivity(pageToOpen);
    }

    //todo add field constrains/validations
    public void login(View view) {
        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        client.login(username, password);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.LOGIN_RESULT) {

            //todo handle other result cases, open lobby

            if (userData != null && (Integer) userData == Result.OK) {

                //todo remove (temporary used for testing chess game-play)
                client.createGame("dada", Color.WHITE);
                PlayChessActivity.isFlipped = false;

                pageToOpen = new Intent(this, PlayChessActivity.class);
                startActivity(pageToOpen);
            }
        }
    }
}

