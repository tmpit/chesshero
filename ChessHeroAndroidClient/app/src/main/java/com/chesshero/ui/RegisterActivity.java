package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;

/**
 * Created by Lyubomira on 11/30/2014.
 */
public class RegisterActivity extends Activity implements EventCenterObserver {

    public static Client client;
    private Intent pageToOpen;
    private TextView exceptionMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        EventCenter.getSingleton().addObserver(this, Client.Event.REGISTER_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
        exceptionMsg = (TextView) findViewById(R.id.registerExceptions);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            pageToOpen = new Intent(this, MainActivity.class);
            startActivity(pageToOpen);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openLoginPage(View view) {
        pageToOpen = new Intent(this, MainActivity.class);
        startActivity(pageToOpen);
        finish();
    }

    //todo add more password/username constraints/validations
    public void register(View view) {
        String username = ((EditText) findViewById(R.id.reg_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.reg_password)).getText().toString();
        String password2 = ((EditText) findViewById(R.id.reg_password2)).getText().toString();

        if (username.trim().length() == 0 || password.length() == 0 || password2.trim().length() == 0) {
            exceptionMsg.setText(" *Please, fill in all the required fields ");
            return;
        }
        if (!password.equals(password2)) {
            exceptionMsg.setText(" *Passwords does not match ");
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
                finish();
            } else if (userData != null && (Integer) userData == Result.INTERNAL_ERROR) {
                exceptionMsg.setText(" *Server error ");
            } else if (userData != null && (Integer) userData == Result.BAD_USER) {
                exceptionMsg.setText(" *Bad user ");
            } else if (userData != null && (Integer) userData == Result.INVALID_NAME) {
                exceptionMsg.setText(" *Invalid username ");
            } else if (userData != null && (Integer) userData == Result.INVALID_PASS) {
                exceptionMsg.setText(" *Invalid password ");
            } else if (userData != null && (Integer) userData == Result.USER_EXISTS) {
                exceptionMsg.setText(" *This user already had an account registered ");
            }
        }
    }
}