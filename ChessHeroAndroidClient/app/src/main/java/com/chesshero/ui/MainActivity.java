package com.chesshero.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.client.ChessHeroApplication;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;

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
        CreateGameActivity.client = client;
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitGameDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openRegisterPage(View view) {
        pageToOpen = new Intent(this, RegisterActivity.class);
        startActivity(pageToOpen);
        finish();
    }

    public void login(View view) {
        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        if (username.trim().length() == 0 || password.length() == 0) {
            TextView lastMsg = (TextView) findViewById(R.id.exceptions);
            lastMsg.setText(" Please, fill in all the required fields ");
            return;
        }
        client.login(username, password);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.LOGIN_RESULT) {

            //todo handle other result cases, open lobby

            if (userData != null && (Integer) userData == Result.OK) {

                pageToOpen = new Intent(this, LobbyActiviy.class);
                startActivity(pageToOpen);
                finish();
            }
        }
    }

    private void showExitGameDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        System.exit(0);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit ChessHero?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}