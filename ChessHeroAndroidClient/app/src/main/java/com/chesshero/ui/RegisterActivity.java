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
 * Created by Lyubomira & Vasil on 11/30/2014.
 * Handles register page logic
 */
public class RegisterActivity extends Activity implements EventCenterObserver {

    /**
     * Game client
     */
    public static Client client;

    /**
     * Next activity to be started
     */
    private Intent pageToOpen;

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
        setContentView(R.layout.register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        exceptionMsg = (TextView) findViewById(R.id.registerExceptions);
        // init client service
        subscribeForGameClientEvents();
    }

    /**
     * Used to handle android's back button clicks
     *
     * @param keyCode KEYCODE_BACK
     * @param event   event
     * @return navigates back to login page
     */
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

    /**
     * Executes when login text hyperlink is clicked
     * Navigates back to login page
     *
     * @param view login text hyperlink
     */
    public void openLoginPage(View view) {
        unsubscribeFromGameClientEvents();
        pageToOpen = new Intent(this, MainActivity.class);
        startActivity(pageToOpen);
        finish();
    }

    /**
     * Executes when register button is clicked
     * Sends a register request to the server
     *
     * @param view register btn
     */
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

    /**
     * Listens for server's response
     *
     * @param eventName event name
     * @param userData  user data
     */
    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.REGISTER_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                unsubscribeFromGameClientEvents();
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
                exceptionMsg.setText(" *A user with that username already exists");
            }
        }
    }

    /**
     * Subscribes for the game client events
     * For this activity: REGISTER_RESULT, LOGIN_RESULT
     */
    private void subscribeForGameClientEvents() {
        EventCenter.getSingleton().addObserver(this, Client.Event.REGISTER_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGIN_RESULT);
    }

    /**
     * Unsubscribe from the game client events
     */
    private void unsubscribeFromGameClientEvents() {
        EventCenter.getSingleton().removeObserver(this, Client.Event.REGISTER_RESULT);
        EventCenter.getSingleton().removeObserver(this, Client.Event.LOGIN_RESULT);
    }
}