package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.client.ChessHeroApplication;
import com.chesshero.client.Client;
import com.chesshero.client.parsers.GameTicket;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;

import java.util.List;

public class LobbyActiviy extends Activity implements EventCenterObserver {

    private Client client;
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_page);
        client = ((ChessHeroApplication) getApplication()).getClient();
        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.PENDING_GAMES_LOAD_RESULT);
        table = (TableLayout) findViewById(R.id.table);
    }


    public void create(View view) {

        //TODO
    }

    public void refresh(View view) {
        client.loadPendingGames();
        TableRow titleRow = (TableRow) findViewById(R.id.row_title);
        TableRow subtitleRow = (TableRow) findViewById(R.id.row_subtitle);

        table.removeAllViews();
        table.addView(titleRow);
        table.addView(subtitleRow);
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {

        if (eventName == Client.Event.PENDING_GAMES_LOAD_RESULT) {


            if (userData != null && (Integer) userData == Result.OK) {
                if (client.getCachedPendingGames() != null) {
                    List<GameTicket> games = client.getCachedPendingGames();

                    for (GameTicket game : games) {
                        TableRow gamesRow = (TableRow) findViewById(R.id.row_games);
                        TextView gameName = (TextView) findViewById(R.id.txt_game);
                        TextView createdBy = (TextView) findViewById(R.id.txt_creator);
                        TextView oponent = (TextView) findViewById(R.id.txt_oponent);

                        gameName.setText(game.gameName);
                        createdBy.setText(game.opponentName);
                        oponent.setText(client.getPlayer().getName());

                        table.addView(gamesRow);
                    }

                } else {

                }
            }
        }


    }
}
