package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        PlayChessActivity.client = client;
        RegisterActivity.client = client;
        LobbyActiviy.client = client;
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


        if (username.trim().length() == 0 || password.length() == 0) {
            TextView lastMsg = (TextView)findViewById(R.id.exceptions);
            lastMsg.setText(" Fill in all the required fields ");
            return;
        } else{
            client.login(username, password);

    }
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.LOGIN_RESULT) {

            //todo handle other result cases, open lobby

            if (userData != null && (Integer) userData == Result.OK) {

                pageToOpen = new Intent(this, LobbyActiviy.class);
                startActivity(pageToOpen);
            }
        }
    }
}

