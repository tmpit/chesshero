package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.ui.chessboard.ChessboardAdapter;
import com.chesshero.ui.chessboard.Moves;
import com.chesshero.ui.chessboard.Tile;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity {

    private GridView grid;
    private Moves moves;
    private boolean isFlipped = true;

    private Tile previousTileClicked;
    private Tile currentTileClicked;
    private boolean newMove = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_chess);

        final ChessboardAdapter adapter = new ChessboardAdapter(PlayChessActivity.this, isFlipped);
        moves = new Moves(adapter.getAllTiles());

        grid = (GridView) findViewById(R.id.chessboard_grid);
        grid.setAdapter(adapter);

        final TextView playerName = (TextView) findViewById(R.id.playerName);
        final TextView oponentName = (TextView) findViewById(R.id.oponentName);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                currentTileClicked = (Tile) view;

                if (currentTileClicked.isMine()) {
                    moves.clear();
                    moves.apply(currentTileClicked);
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

                    currentTileClicked.setTileImage(previousTileClicked.getTileImage());
                    previousTileClicked.setTileImage(0);
                    moves.clear();
                    newMove = true;
                }
                //todo remove this after we are done coding (used for debugging)
                playerName.setText("Position: " + position);
                oponentName.setText(currentTileClicked.toString());
            }
        });
    }
}

