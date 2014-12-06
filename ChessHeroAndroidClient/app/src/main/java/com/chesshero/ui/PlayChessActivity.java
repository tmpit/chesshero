package com.chesshero.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.chesshero.R;
import com.chesshero.ui.chessboard.Chessboard;
import com.chesshero.ui.chessboard.Tile;

/**
 * Created by Vasil on 30.11.2014 г..
 */
public class PlayChessActivity extends Activity {

    private String mPlayerName = "proba player";
    private String mOponentName = "proba oponent";

    //todo
    GridView grid;
    Tile previousTileClicked;
    Tile currentTileClicked;
    int moveCounter = 1;
    boolean firstMove = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_chess);

        Chessboard adapter = new Chessboard(PlayChessActivity.this);
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

                if (moveCounter == 1) {
                    moveCounter++;
                    firstMove = true;
                } else if (moveCounter == 2) {
                    moveCounter = 1;
                    firstMove = false;
                }

                if (firstMove) {

                    if (currentTileClicked.getTileImage() == 0) {
                        Toast.makeText(PlayChessActivity.this, "Please select a chess piece", Toast.LENGTH_SHORT).show();
                        moveCounter = 1;
                        firstMove = false;
                        return;
                    }

                    previousTileClicked = currentTileClicked;
                } //if not first move, then move the piece from the first click to the second one.
                else {
                    currentTileClicked.setImageResource(previousTileClicked.getTileImage());
                    previousTileClicked.setImageResource(0);
                }

                //todo remove this after we are done coding (used for debugging)
                Toast.makeText(PlayChessActivity.this, "Position: " + position
                        + "\n" + currentTileClicked.toString()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
