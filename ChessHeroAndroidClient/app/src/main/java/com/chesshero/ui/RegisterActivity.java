package com.chesshero.ui;

/**
 * Created by Lyubomira on 11/30/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;


public class RegisterActivity extends Activity implements EventCenterObserver {

    public static Client client;
    private Intent pageToOpen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EventCenter.getSingleton().addObserver(this, Client.Event.REGISTER_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
    }

    public void openLoginPage(View view) {
        pageToOpen = new Intent(this, MainActivity.class);
        startActivity(pageToOpen);
    }

    //todo add more password/username constraints/validations
    public void register(View view) {
        String username = ((EditText) findViewById(R.id.reg_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.reg_password)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.reg_password2)).getText().toString();

        if (!password.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        client.register(username, password);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.REGISTER_RESULT) {

            if (userData != null && (Integer) userData == Result.OK) {
                pageToOpen = new Intent(this, LobbyActiviy.class);
                startActivity(pageToOpen);
            }
        }
    }
}