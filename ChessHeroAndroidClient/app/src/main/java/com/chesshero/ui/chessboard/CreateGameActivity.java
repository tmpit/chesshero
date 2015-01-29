package com.chesshero.ui.chessboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.chesshero.ui.LobbyActiviy;
import com.chesshero.ui.MainActivity;
import com.kt.api.Result;

/**
 * Created by Lyubomira on 1/26/2015.
 */
public class CreateGameActivity extends Activity implements EventCenterObserver {


    public static Client client;
    private TableLayout table;
    private Intent pageToOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_page);


        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);


    }





    public void backToLobby(View view){

        pageToOpen = new Intent(this, LobbyActiviy.class);
        startActivity(pageToOpen);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {

        //  if (eventName == Client.Event.CREATE_GAME_RESULT) {
        //  if (userData != null && (Integer) userData == Result.OK) {}
    }
}