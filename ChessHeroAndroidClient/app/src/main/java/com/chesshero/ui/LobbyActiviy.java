package com.chesshero.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
        EventCenter.getSingleton().addObserver(this, Client.Event.JOIN_GAME_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.PENDING_GAMES_LOAD_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.LOGOUT);
        refreshGames();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            client.logout();
            pageToOpen = new Intent(this, MainActivity.class);
            startActivity(pageToOpen);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void logout(View view) {
        client.logout();
        pageToOpen = new Intent(this, MainActivity.class);
        startActivity(pageToOpen);
        finish();
    }

    public void joinGame(GameTicket gameTicket) {
        client.joinGame(gameTicket);
    }

    public void createGame(View view) {
        pageToOpen = new Intent(this, CreateGameActivity.class);
        startActivity(pageToOpen);
        finish();
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
                } else {
                    TableRow noGamesRow = new TableRow(this);
                    TextView noGamesTxt = new TextView(this);
                    noGamesTxt.setText("No pending games");
                    noGamesTxt.setTextColor(Color.RED);
                    noGamesTxt.setGravity(Gravity.CENTER);
                    noGamesRow.addView(noGamesTxt);
                    table.addView(noGamesRow);
                }
            }
        } else if (eventName == Client.Event.JOIN_GAME_RESULT) {
            if (userData != null && (Integer) userData == Result.OK) {
                joinGame();
            } else if ((userData != null && (Integer) userData == Result.ALREADY_PLAYING) ||
                    (userData != null && (Integer) userData == Result.INVALID_GAME_ID) ||
                    (userData != null && (Integer) userData == Result.MISSING_PARAMETERS) ||
                    (userData != null && (Integer) userData == Result.INTERNAL_ERROR)) {
                Toast.makeText(LobbyActiviy.this, "Game not found", Toast.LENGTH_SHORT).show();
                refreshGames();
            }
        } else if (eventName == Client.Event.LOGOUT) {

        }
    }

    private void joinGame() {
        PlayChessActivity.isFlipped = client.getGame().getBlackPlayer().equals(client.getPlayer());
        pageToOpen = new Intent(this, PlayChessActivity.class);
        startActivity(pageToOpen);
        finish();
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
            TextView opponent = new TextView(this);

            gameName.setText(game.gameName);
            gameName.setTextColor(Color.BLACK);
            gameName.setGravity(Gravity.CENTER);
            createdBy.setText(game.opponentName);
            createdBy.setTextColor(Color.BLACK);
            createdBy.setGravity(Gravity.CENTER);
            opponent.setText(game.opponentColor.toString());
            opponent.setTextColor(Color.BLACK);
            opponent.setGravity(Gravity.CENTER);

            if (table.getChildCount() % 2 == 0) {
                gamesRow.setBackgroundColor(Color.YELLOW);
            }
            gamesRow.setPadding(0, 12, 0, 12);
            gamesRow.addView(gameName);
            gamesRow.addView(createdBy);
            gamesRow.addView(opponent);
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
