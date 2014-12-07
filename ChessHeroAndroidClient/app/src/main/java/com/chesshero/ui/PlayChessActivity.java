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
import com.chesshero.ui.chessboard.Tile;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity {

    private final ChessboardAdapter adapter = new ChessboardAdapter(PlayChessActivity.this);

    private GridView grid;
    private String mPlayerName = "proba player";
    private String mOponentName = "proba oponent";
    private Tile previousTileClicked;
    private Tile currentTileClicked;
    private boolean newMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_chess);


        grid = (GridView) findViewById(R.id.chessboard_grid);
        grid.setAdapter(adapter);

        TextView playerName = (TextView) findViewById(R.id.playerName);
        playerName.setText(mPlayerName);

        TextView oponentName = (TextView) findViewById(R.id.oponentName);
        oponentName.setText(mOponentName);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                currentTileClicked = (Tile) view;

                if (newMove) {
                    if (currentTileClicked.getTileImage() == 0) {
                        Toast.makeText(PlayChessActivity.this, "Please select a chess piece", Toast.LENGTH_SHORT).show();
                        newMove = true;
                        return;
                    }
                    previousTileClicked = currentTileClicked;
                    newMove = false;
                } //if not new move, then move the piece from the first click to the second one.
                else {
                    if (currentTileClicked.equals(previousTileClicked)) {
                        return;
                    }
                    currentTileClicked.setTileImage(previousTileClicked.getTileImage());
                    previousTileClicked.setTileImage(0);
                    newMove = true;
                }
                //todo remove this after we are done coding (used for debugging)
                Toast.makeText(PlayChessActivity.this, "Position: " + position
                        + "\n" + currentTileClicked, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

