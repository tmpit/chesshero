package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.client.parsers.GameTicket;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.kt.api.Result;

import java.util.List;

public class LobbyActiviy extends Activity implements EventCenterObserver {

    public static Client client;
    private TableLayout table;
    private Intent pageToOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_page);
        table = (TableLayout) findViewById(R.id.table);

        EventCenter.getSingleton().addObserver(this, Client.Event.CREATE_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.PENDING_GAMES_LOAD_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_PUSH);
        refreshGames();
    }

    public void joinGame(GameTicket gameTicket) {
        client.joinGame(gameTicket);
    }

    public void create(View view) {

        //TODO
    }

    public void refresh(View view) {
        refreshGames();
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {

        if (eventName == Client.Event.PENDING_GAMES_LOAD_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                if (client.getCachedPendingGames() != null) {
                    loadGames();
                }
            } else {
                //todo
            }
        } else if (eventName == Client.Event.JOIN_GAME_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                // todo game not starting?
                // request is completed successfully ?!?
                // playchessactivity still not starting...
                joinGame();
            }
        }
    }

    private void joinGame() {
        PlayChessActivity.isFlipped = client.getGame().getBlackPlayer().equals(client.getPlayer());

        pageToOpen = new Intent(this, PlayChessActivity.class);
        startActivity(pageToOpen);
    }

    private void refreshGames() {
        client.loadPendingGames();
        TableRow titleRow = (TableRow) findViewById(R.id.row_title);
        TableRow subtitleRow = (TableRow) findViewById(R.id.row_subtitle);

        table.removeAllViews();
        table.addView(titleRow);
        table.addView(subtitleRow);
    }

    private void loadGames() {
        List<GameTicket> games = client.getCachedPendingGames();

        for (final GameTicket game : games) {
            TableRow gamesRow = new TableRow(this);
            TextView gameName = new TextView(this);
            TextView createdBy = new TextView(this);
            TextView oponent = new TextView(this);

            gameName.setText(game.gameName);
            createdBy.setText(game.opponentName);
            oponent.setText(client.getPlayer().getName());

            gamesRow.addView(gameName);
            gamesRow.addView(createdBy);
            gamesRow.addView(oponent);
            gamesRow.setClickable(true);
            gamesRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGame(game);
                }
            });
            table.addView(gamesRow);
        }
    }
}
