package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;

/**
 * Created by Lyubomira on 1/26/2015.
 */
public class CreateGameActivity extends Activity implements EventCenterObserver {

    public static Client client;
    private Intent pageToOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_page);

        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);

    }

    public void backToLobby(View view) {
        pageToOpen = new Intent(this, LobbyActiviy.class);
        startActivity(pageToOpen);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {

        //  if (eventName == Client.Event.CREATE_GAME_RESULT) {
        //  if (userData != null && (Integer) userData == Result.OK) {}
    }
}