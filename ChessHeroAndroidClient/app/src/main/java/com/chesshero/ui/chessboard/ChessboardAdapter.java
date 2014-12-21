package com.chesshero.ui.chessboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chesshero.R;

/**
 * Created by Vasil on 6.12.2014 г..
 */
public class ChessboardAdapter extends BaseAdapter {

    private static final int ROWS = 8;

    private static final int COLS = 8;

    private Tile[][] allTiles = new Tile[ROWS][COLS];

    private Tile[] allTilesByPosition = new Tile[ROWS*COLS];

    private Context mContext;

    private View mGrid;

    private boolean mIsFlipped;

    public ChessboardAdapter(Context context, boolean isFlipped) {
        mContext = context;
        mIsFlipped = isFlipped;
    }

    public Tile[][] getAllTiles() {
        return allTiles;
    }

    @Override
    public int getCount() {
        return ROWS * COLS;
    }

    @Override
    public Object getItem(int position) {
        return allTilesByPosition[position];
    }

    @Override
    public long getItemId(int position) {
        return allTilesByPosition[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            mGrid = inflater.inflate(R.layout.tile, null);

            Tile tile = (Tile) mGrid.findViewById(R.id.single_tile);
            tile.initTile(position, mIsFlipped);

            allTiles[tile.getRow()][tile.getCol()] = tile;
            allTilesByPosition[position] = tile;
        } else {
            mGrid = convertView;
        }
        return mGrid;
    }
}
