package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.client.Client;
import com.chesshero.event.EventCenter;
import com.chesshero.event.EventCenterObserver;
import com.chesshero.ui.chessboard.ChessboardAdapter;
import com.chesshero.ui.chessboard.Restrictions;
import com.chesshero.ui.chessboard.Tile;
import com.kt.game.Move;

import java.util.List;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity implements EventCenterObserver {

    public static boolean isFlipped = false;
    private final ChessboardAdapter adapter = new ChessboardAdapter(PlayChessActivity.this, isFlipped);
    public static Client client;
    private GridView grid;
    private Restrictions restrictions;
    private Tile previousTileClicked;
    private Tile currentTileClicked;
    private boolean newMove = true;
    private boolean isMyTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_chess);

        // init client service
        EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_RESULT);
        EventCenter.getSingleton().addObserver(this, Client.Event.MOVE_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.END_GAME_PUSH);
        EventCenter.getSingleton().addObserver(this, Client.Event.EXIT_GAME_RESULT);

        // set player names
        final TextView playerName = (TextView) findViewById(R.id.playerName);
        final TextView oponentName = (TextView) findViewById(R.id.oponentName);
        playerName.setText(client.getPlayer().getName());
        oponentName.setText(client.getPlayer().getOpponent().getName());

        grid = (GridView) findViewById(R.id.chessboard_grid);
        grid.setAdapter(adapter);
        restrictions = new Restrictions(adapter.getAllTiles());

        if (isFlipped) {
            grid.setBackgroundResource(R.drawable.board_flipped);
            isMyTurn = false;
        } else {
            grid.setBackgroundResource(R.drawable.board);
        }

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                currentTileClicked = (Tile) view;

                if (!isMyTurn) {
                    Toast.makeText(PlayChessActivity.this, "Not your turn", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentTileClicked.isMine()) {
                    restrictions.clear();
                    restrictions.apply(currentTileClicked);
                    newMove = true;
                }

                if (newMove) {
                    if (currentTileClicked.isEmpty() || currentTileClicked.isOponent()) {
                        Toast.makeText(PlayChessActivity.this, "Please select a your chess piece", Toast.LENGTH_SHORT).show();
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
                    //todo log
                    boolean capture = currentTileClicked.isOponent();
                    // x - captured
                    // > - moved to
//                    oponentName.setText(currentTileClicked.getTileImageName() + " "
//                            + previousTileClicked.toString()
//                            + (capture ? " x " : " > ")
//                            + currentTileClicked.toString());

                    client.executeMove(previousTileClicked.getPosition(), currentTileClicked.getPosition());
                    restrictions.clear();
                    newMove = true;
                }
//                //todo remove this after we are done coding (used for debugging)
//                playerName.setText("Position: " + position + "\n"
//                        + currentTileClicked.getPosition().toString());
            }
        });

//        final VerticalPager pager = (VerticalPager) findViewById(R.id.pager);
//        final LinearLayout list = (LinearLayout) findViewById(R.id.log);
//
//        TextView text;
//
//        for(int i = 0; i < 40; i++ ) {
//            text = new TextView(this);
//            text.setText("test: "+i);
//            text.setTextSize(30);
//            list.addView(text);
//        }
//
//        pager.addOnScrollListener(new VerticalPager.OnScrollListener() {
//            public void onScroll(int scrollX) {
//                //Log.d("TestActivity", "scrollX=" + scrollX);
//            }
//
//            public void onViewScrollFinished(int currentPage) {
//                //Log.d("TestActivity", "viewIndex=" + currentPage);
//            }
//        });
    }

    @Override
    public void eventCenterDidPostEvent(String eventName, Object userData) {
        List<Move> moves = client.getGame().getExecutedMoves();

        if (eventName == Client.Event.MOVE_RESULT) {
            drawMove(moves.get(moves.size() - 1));
            isMyTurn = false;
            //vij drugite raoti tuk w game obekta
            //  Log.i("", currentLastMove.executor.getName());
        }
        if (eventName == Client.Event.MOVE_PUSH) {
            drawMove(moves.get(moves.size() - 1));
            isMyTurn = true;
        }
    }

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

        Tile previousTile = adapter.getTileAt(startRow, startCol);
        Tile currentTile = adapter.getTileAt(endRow, endCol);

        currentTile.setTileImageId(previousTile.getTileImageId());
        previousTile.setTileImageId(0);

        Log.i("PlayChessActivity",
                String.format("drawing move from {%d,%d} to {%d,%d}", startRow, startCol, endRow, endCol));
    }
}

