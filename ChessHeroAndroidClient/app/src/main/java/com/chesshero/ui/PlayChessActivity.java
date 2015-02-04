package com.chesshero.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.client.Promotion;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.chesshero.ui.chessboard.ChessboardAdapter;
import com.chesshero.ui.chessboard.Restrictions;
import com.chesshero.ui.chessboard.Tile;
import com.kt.game.Game;
import com.kt.game.Move;

import java.util.List;

/**
 * Created by Vasil on 30.11.2014 г..
 * Play game & chessboard logic class
 */
public class PlayChessActivity extends Activity implements EventCenterObserver {

    /**
     * Game client
     */
    public static Client client;

    /**
     * True if the user is playing with black chess piece set, else false
     */
    public static boolean isFlipped = false;

    /**
     * Adapter used to inflate chess tiles on the board
     */
    private final ChessboardAdapter ADAPTER = new ChessboardAdapter(PlayChessActivity.this,
            isFlipped, client.getPlayer().getGame().getBoard());

    /**
     * Layout inflater
     */
    private LayoutInflater inflater;

    /**
     * Device's display width
     */
    private int windowWidth;

    /**
     * Device's display height
     */
    private int windowHeight;

    /**
     * The chessboard gird
     */
    private GridView grid;

    /**
     * Used to apply move restrictions
     */
    private Restrictions restrictions;

    /**
     * Holding the previous tile clicked
     */
    private Tile previousTileClicked;

    /**
     * Holding the current tile clicked
     */
    private Tile currentTileClicked;

    /**
     * Indicates if it is a start of a new move
     */
    private boolean newMove = true;

    /**
     * Used to check if player is in turn
     */
    private boolean isMyTurn = true;

    /**
     * Signifies that the log menu is already visible
     */
    private boolean isLogAlreadyShowing = false;

    /**
     * Reference of the {@link android.widget.PopupWindow} which dims the screen
     */
    private PopupWindow fadePopup;

    /**
     * The translate animation
     */
    private Animation animation;

    /**
     * The view which needs to be translated
     */
    private LinearLayout baseView;

    /**
     * Initializes all variables and sets listeners
     *
     * @param savedInstanceState saved state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_chess);
        // init client service
        subscribeForGameClientEvents();

        // get inflater and display parameters
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Display display = getWindowManager().getDefaultDisplay();
        windowHeight = display.getHeight();
        windowWidth = display.getWidth();

        // set on click listener to for the log button
        findViewById(R.id.log_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogAlreadyShowing) {
                    isLogAlreadyShowing = true;
                    openSlidingMenu();
                }
            }
        });

        // set player names
        final TextView playerName = (TextView) findViewById(R.id.playerName);
        final TextView oponentName = (TextView) findViewById(R.id.oponentName);
        playerName.setText(client.getPlayer().getName());
        oponentName.setText(client.getPlayer().getOpponent().getName());

        // init chessboard grid and apply its adapter
        grid = (GridView) findViewById(R.id.chessboard_grid);
        grid.setAdapter(ADAPTER);
        restrictions = new Restrictions(ADAPTER.getAllTiles());

        // handle straight and flipped cases
        if (isFlipped) {
            grid.setBackgroundResource(R.drawable.board_flipped);
            isMyTurn = false;
        } else {
            grid.setBackgroundResource(R.drawable.board);
        }

        // set on item click listener for the chessboard grid tiles
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                currentTileClicked = (Tile) view;

                if (!isMyTurn) {
                    Toast.makeText(PlayChessActivity.this, "It is not your turn", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentTileClicked.isMine()) {
                    restrictions.clear();
                    restrictions.apply(currentTileClicked);
                    newMove = true;
                }
                if (newMove) {
                    if (currentTileClicked.isEmpty() || currentTileClicked.isOponent()) {
                        Toast.makeText(PlayChessActivity.this, "This is not your chess piece", Toast.LENGTH_SHORT).show();
                        newMove = true;
                        return;
                    }
                    previousTileClicked = currentTileClicked;
                    newMove = false;
                } //if not new move, then move the piece from the first click to the second one.
                else {
                    if (!currentTileClicked.isAvailable()) {
                        Toast.makeText(PlayChessActivity.this, "Illegal move", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    client.executeMove(previousTileClicked.getPosition(), currentTileClicked.getPosition(), Promotion.QUEEN);
                    restrictions.clear();
                    newMove = true;
                }
            }
        });
    }

    /**
     * Used to handle android's back button clicks
     *
     * @param keyCode KEYCODE_BACK
     * @param event   event
     * @return {@link #showLeaveGameDialog}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showLeaveGameDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Executes when leave game button is clicked
     *
     * @param view leave game btn
     */
    public void leaveGame(View view) {
        showLeaveGameDialog();
    }

    /**
     * Listens for server's response
     *
     * @param eventName event name
     * @param userData  user data
     */
    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {

        List<Move> moves = client.getGame().getExecutedMoves();

        if (eventName == Client.Event.MOVE_RESULT) {
            drawMove(moves.get(moves.size() - 1));
            isMyTurn = false;
        } else if (eventName == Client.Event.MOVE_PUSH) {
            drawMove(moves.get(moves.size() - 1));
            isMyTurn = true;
        } else if (eventName == Client.Event.EXIT_GAME_RESULT) {
            navigateToLobby();
        } else if (eventName == Client.Event.END_GAME_PUSH) {
            showGameEndDialogue(client.getGame().getWinner().equals(client.getPlayer()), client.getGame().getEnding());
        }
    }

    /**
     * Subscribes for the game client events
     * For this activity: MOVE_RESULT, MOVE_PUSH, END_GAME_PUSH, EXIT_GAME_RESULT
     */
    private void subscribeForGameClientEvents() {
        EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.END_GAME_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.EXIT_GAME_RESULT);
    }

    /**
     * Unsubscribe from the game client events
     */
    private void unsubscribeFromGameClientEvents() {
        EventCenter.getSingleton().removeObserver(this, Client.Event.MOVE_RESULT);
        EventCenter.getSingleton().removeObserver(this, Client.Event.MOVE_PUSH);
        EventCenter.getSingleton().removeObserver(this, Client.Event.END_GAME_PUSH);
        EventCenter.getSingleton().removeObserver(this, Client.Event.EXIT_GAME_RESULT);
    }

    /**
     * Drawing a move after the server posts a
     * MOVE_RESULT or a MOVE_PUSH event
     *
     * @param move object containing the move code
     */
    private void drawMove(Move move) {
        int startCol, startRow, endCol, endRow;
        // flipped - true
        if (isFlipped) {
            startCol = 104 - move.code.charAt(0);
            startRow = Integer.parseInt(move.code.charAt(1) + "") - 1;
            endCol = 104 - move.code.charAt(2);
            endRow = Integer.parseInt(move.code.charAt(3) + "") - 1;
        } else {
            // flipped - false
            startCol = move.code.charAt(0) - 97;
            startRow = 8 - Integer.parseInt(move.code.charAt(1) + "");
            endCol = move.code.charAt(2) - 97;
            endRow = 8 - Integer.parseInt(move.code.charAt(3) + "");
        }

        Tile startTile = ADAPTER.getTileAt(startRow, startCol);
        Tile endTile = ADAPTER.getTileAt(endRow, endCol);
        // moving image from start to end location
        endTile.setTileImageId(startTile.getTileImageId());
        startTile.setTileImageId(0);

        Log.i("PlayChessActivity",
                String.format("drawing move from {%d,%d} to {%d,%d}", startRow, startCol, endRow, endCol));
    }

    /**
     * Closes PlayChessActivity and opens LobbyActiviy
     */
    private void navigateToLobby() {
        unsubscribeFromGameClientEvents();
        startActivity(new Intent(this, LobbyActiviy.class));
        finish();
    }

    /**
     * Shows end game dialog
     *
     * @param iAmWinner used to determinate victory or loss
     * @param ending    can be either CHECKMATE, SUDDED_DEATH or SURRENDER
     */
    private void showGameEndDialogue(boolean iAmWinner, Game.Ending ending) {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                navigateToLobby();
            }
        };

        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                navigateToLobby();
            }
        };

        String message = "";

        switch (ending) {
            case CHECKMATE:
                message = "Checkmate! ";
                break;

            case SUDDED_DEATH:
                message = "Time is up! ";
                break;

            case SURRENDER:
                // If a player surrenders, only the winner receives a game end event
                message = "Your opponent has surrendered! ";
                break;
        }

        if (iAmWinner) {
            message += "You won!";
        } else {
            message += "You lost.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Go back to lobby", clickListener)
                .setOnCancelListener(cancelListener)
                .show();
    }

    /**
     * Shows leave game dialog
     */
    private void showLeaveGameDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        client.exitGame();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the current game?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    /**
     * Opens the sliding Menu
     */
    private void openSlidingMenu() {
        showFadePopup();
        // The amount of view which needs to be moved out. equivalent to the
        // width of the menu
        int width = RelativeLayout.LayoutParams.FILL_PARENT;
        int height = (int) (windowHeight * 0.30f);
        translateView((float) height);
        // creating a popup
        final View layout = inflater.inflate(R.layout.moves_log, (ViewGroup) findViewById(R.id.moves_log_layout));
        // populating move entries
        LinearLayout list = (LinearLayout) layout.findViewById(R.id.moves_log_layout);
        List<Move> moves = client.getGame().getExecutedMoves();
        for (Move move : moves) {
            TextView text = new TextView(this);
            text.setText(move.executor.getName() + ":  " + move.code);
            text.setTextSize(18);
            text.setTextColor(Color.WHITE);
            text.setGravity(Gravity.CENTER);
            list.addView(text);
        }

        final PopupWindow movesLogPopup = new PopupWindow(layout, width, height, true);
        movesLogPopup.setBackgroundDrawable(new PaintDrawable());
        movesLogPopup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);

        movesLogPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                fadePopup.dismiss();
                cleanUp();
                translateView(0);
                cleanUp();
                isLogAlreadyShowing = false;
            }
        });
    }

    /**
     * This method is responsible for view translation. It applies a translation
     * animation on the root view of the activity
     *
     * @param moveTo The position to translate to
     */
    private void translateView(float moveTo) {
        animation = new TranslateAnimation(0f, 0f, 0f, moveTo);
        animation.setDuration(300);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);

        baseView = (LinearLayout) findViewById(R.id.playChessView);
        baseView.startAnimation(animation);
        baseView.setVisibility(View.VISIBLE);
    }

    /**
     * Fades the entire screen, gives a dim background
     */
    private void showFadePopup() {
        final View layout = inflater.inflate(R.layout.fadepopup, (ViewGroup) findViewById(R.id.fadePopup));
        fadePopup = new PopupWindow(layout, windowWidth, windowHeight, false);
        fadePopup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * Basic cleanup to avoid memory issues. Not everything is release after
     * animation, so to immediately release it doing it manually
     */
    private void cleanUp() {
        if (null != baseView) {
            baseView.clearAnimation();
            baseView = null;
        }
        if (null != animation) {
            animation.cancel();
            animation = null;
        }
        fadePopup = null;
    }
}

