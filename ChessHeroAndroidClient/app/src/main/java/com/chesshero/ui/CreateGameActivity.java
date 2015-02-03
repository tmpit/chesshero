package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;
import com.kt.game.Color;

/**
 * Created by Lyubomira & Vasil on 1/26/2015.
 * Handles create game page logic
 */
public class CreateGameActivity extends Activity implements EventCenterObserver {

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
     * Create or Cancel game button
     */
    private Button createBtn;

    /**
     * Back to lobby button
     */
    private Button backToLobbyBtn;

    /**
     * Create game button label
     */
    private String createGameText = "Create Game";

    /**
     * Cancel game button label
     */
    private String cancelGameText = "Cancel Game";

    /**
     * Initializes all clients
     *
     * @param savedInstanceState saved state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_page);
        createBtn = (Button) findViewById(R.id.create_cancel_game_btn);
        backToLobbyBtn = (Button) findViewById(R.id.back_to_lobby_btn);
        exceptionMsg = (TextView) findViewById(R.id.createGameExceptions);
        // init client service
        subscribeForGameClientEvents();
    }

    /**
     * Used to handle android's back button clicks
     *
     * @param keyCode KEYCODE_BACK
     * @param event   event
     * @return navigates back to lobby page
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (createBtn.getText().toString().equals(cancelGameText)) {
                client.cancelGame();
            }
            unsubscribeFromGameClientEvents();
            pageToOpen = new Intent(this, LobbyActiviy.class);
            startActivity(pageToOpen);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Executes when back to lobby button is clicked
     * Navigates back to lobby page
     *
     * @param view back to lobby btn
     */
    public void backToLobby(View view) {
        unsubscribeFromGameClientEvents();
        pageToOpen = new Intent(this, LobbyActiviy.class);
        startActivity(pageToOpen);
        finish();
    }

    /**
     * Executes when create game button is clicked
     * Sends a createGame request to the server
     *
     * @param view create game btn
     */
    public void createGame(View view) {
        if (createBtn.getText().toString().equals(createGameText)) {
            EditText gameName = (EditText) findViewById(R.id.game_name_txt);
            if (gameName.getText().length() == 0) {
                Toast.makeText(CreateGameActivity.this, "Please enter game name", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioGroup selectedColor = (RadioGroup) findViewById(R.id.color_radio_grp);
            RadioButton whiteColor = (RadioButton) findViewById(R.id.playWhite);
            if (selectedColor.getCheckedRadioButtonId() == whiteColor.getId()) {
                client.createGame(gameName.getText().toString(), Color.WHITE);
            } else {
                client.createGame(gameName.getText().toString(), Color.BLACK);
            }
        } else {
            client.cancelGame();
        }
    }

    /**
     * Listens for server's response
     *
     * @param eventName event name
     * @param userData  user data
     */
    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        if (eventName == Client.Event.CREATE_GAME_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                createBtn.setText(cancelGameText);
                backToLobbyBtn.setClickable(false);
                backToLobbyBtn.setTextColor(android.graphics.Color.GRAY);
                exceptionMsg.setText(" *Waiting for another player to join ");
                exceptionMsg.setTextColor(android.graphics.Color.GREEN);
            } else if (userData != null && (Integer) userData == Result.INVALID_GAME_NAME) {
                exceptionMsg.setText(" *Invalid game name ");
                exceptionMsg.setTextColor(android.graphics.Color.RED);
            }
        } else if (eventName == Client.Event.CANCEL_GAME_RESULT) {
            createBtn.setText(createGameText);
            backToLobbyBtn.setClickable(true);
            backToLobbyBtn.setTextColor(android.graphics.Color.WHITE);
            exceptionMsg.setText("");
        } else if (eventName == Client.Event.JOIN_GAME_PUSH) {
            PlayChessActivity.isFlipped = client.getGame().getBlackPlayer().equals(client.getPlayer());
            unsubscribeFromGameClientEvents();
            pageToOpen = new Intent(this, PlayChessActivity.class);
            startActivity(pageToOpen);
            finish();
        }
    }

    /**
     * Subscribes for the game client events
     * For this activity: CREATE_GAME_RESULT, JOIN_GAME_PUSH, CANCEL_GAME_RESULT
     */
    private void subscribeForGameClientEvents() {
        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.CANCEL_GAME_RESULT);
    }

    /**
     * Unsubscribe from the game client events
     */
    private void unsubscribeFromGameClientEvents() {
        EventCenter.getSingleton().removeObserver(this, Client.Event.CREATE_GAME_RESULT);
        EventCenter.getSingleton().removeObserver(this, Client.Event.JOIN_GAME_PUSH);
        EventCenter.getSingleton().removeObserver(this, Client.Event.CANCEL_GAME_RESULT);
    }
}