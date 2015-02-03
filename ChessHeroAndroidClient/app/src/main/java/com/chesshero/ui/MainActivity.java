package com.chesshero.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.client.ChessHeroApplication;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;

/**
 * Created by Lyubomira & Vasil on 30.11.2014 г..
 * Starts the application at the login screen
 */
public class MainActivity extends Activity implements EventCenterObserver {

    /**
     * Next activity to be started
     */
    private Intent pageToOpen;

    /**
     * Game client
     */
    private Client client;

    /**
     * Used for showing messages in exceptional cases
     */
    private TextView exceptionMsg;

    /**
     * Initializes all clients, hides the keyboard on startup
     *
     * @param savedInstanceState saved state bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        exceptionMsg = (TextView) findViewById(R.id.loginExceptions);
        // init client service
        initClient(((ChessHeroApplication) getApplication()).getClient());
        subscribeForGameClientEvents();
    }

    /**
     * Used to handle android's back button clicks
     *
     * @param keyCode KEYCODE_BACK
     * @param event   event
     * @return {@link #showExitGameDialog}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitGameDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Executes when register text hyperlink is clicked
     * Navigates to register page
     *
     * @param view register text hyperlink
     */
    public void openRegisterPage(View view) {
        unsubscribeFromGameClientEvents();
        pageToOpen = new Intent(this, RegisterActivity.class);
        startActivity(pageToOpen);
        finish();
    }

    /**
     * Executes when login button is clicked
     * Sends a login request to the server
     *
     * @param view login btn
     */
    public void login(View view) {
        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        if (username.trim().length() == 0 || password.length() == 0) {
            exceptionMsg.setText(" *Please, fill in all the required fields ");
            return;
        }
        client.login(username, password);
    }

    /**
     * Listens for server's response
     *
     * @param eventName event name
     * @param userData  user data
     */
    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.LOGIN_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                unsubscribeFromGameClientEvents();
                pageToOpen = new Intent(this, LobbyActiviy.class);
                startActivity(pageToOpen);
                finish();
            } else if (userData != null && (Integer) userData == Result.INTERNAL_ERROR) {
                exceptionMsg.setText(" *Server error ");
            } else if (userData != null && (Integer) userData == Result.INVALID_CREDENTIALS) {
                exceptionMsg.setText(" *Invalid username or password ");
            } else if (userData != null && (Integer) userData == Result.ALREADY_LOGGEDIN) {
                exceptionMsg.setText(" *This user is already logged in ");
            }
        }
    }

    /**
     * Subscribes for the game client events
     * For this activity: LOGIN_RESULT
     */
    private void subscribeForGameClientEvents() {
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
    }

    /**
     * Unsubscribe from the game client events
     */
    private void unsubscribeFromGameClientEvents() {
        EventCenter.getSingleton().removeObserver(this, Client.Event.LOGIN_RESULT);
    }

    /**
     * Sets client for all activities
     *
     * @param client client
     */
    private void initClient(Client client) {
        this.client = client;
        RegisterActivity.client = client;
        LobbyActiviy.client = client;
        CreateGameActivity.client = client;
        PlayChessActivity.client = client;
    }

    /**
     * Shows exit game dialog
     */
    private void showExitGameDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        unsubscribeFromGameClientEvents();
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