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

    private Context mContext;

    private View mGrid;

    public ChessboardAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return ROWS * COLS;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            mGrid = inflater.inflate(R.layout.tile, null);
            Tile tile = (Tile) mGrid.findViewById(R.id.single_tile);
            tile.initTile(position);
        } else {
            mGrid = convertView;
        }
        return mGrid;
    }
}
