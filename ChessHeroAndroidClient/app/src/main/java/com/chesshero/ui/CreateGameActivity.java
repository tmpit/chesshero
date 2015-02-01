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
 * Created by Lyubomira on 1/26/2015.
 */
public class CreateGameActivity extends Activity implements EventCenterObserver {

    public static Client client;
    private Intent pageToOpen;
    private Button createBtn;
    private Button backToLobbyBtn;
    private String createGameText = "Create Game";
    private String cancelGameText = "Cancel Game";
    private TextView exceptionMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_page);
        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.CANCEL_GAME_RESULT);
        createBtn = (Button) findViewById(R.id.create_cancel_game_btn);
        backToLobbyBtn = (Button) findViewById(R.id.back_to_lobby_btn);
        exceptionMsg = (TextView) findViewById(R.id.createGameExceptions);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (createBtn.getText().toString().equals(cancelGameText)) {
                client.cancelGame();
            }
            pageToOpen = new Intent(this, LobbyActiviy.class);
            startActivity(pageToOpen);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backToLobby(View view) {
        pageToOpen = new Intent(this, LobbyActiviy.class);
        startActivity(pageToOpen);
        finish();
    }

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
            pageToOpen = new Intent(this, PlayChessActivity.class);
            startActivity(pageToOpen);
            finish();
        }
    }
}