package com.chesshero.ui.chessboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chesshero.R;
import com.kt.game.BoardField;

/**
 * Created by Vasil on 6.12.2014 г..
 * Inflates chessboard tiles up on the chessboard grid
 */
public class ChessboardAdapter extends BaseAdapter {

    /**
     * Number of rows
     */
    private static final int ROWS = 8;

    /**
     * Number of columns
     */
    private static final int COLS = 8;

    /**
     * Keeps all tiles, indexed by rows and columns
     */
    private Tile[][] allTiles = new Tile[ROWS][COLS];

    /**
     * Keeps all tiles, indexed by position number
     */
    private Tile[] allTilesByPosition = new Tile[ROWS * COLS];

    /**
     * Context
     */
    private Context mContext;

    /**
     * True if the user is playing with black chess piece set, else false
     */
    private boolean mIsFlipped;

    /**
     * Chessboard filed received from the server
     */
    private BoardField[][] mBoardField;

    /**
     * ChessboardAdapter constructor
     *
     * @param context context
     * @param isFlipped see {@link #mIsFlipped}
     * @param boardField see {@link #mBoardField}
     */
    public ChessboardAdapter(Context context, boolean isFlipped, BoardField[][] boardField) {
        mContext = context;
        mIsFlipped = isFlipped;
        mBoardField = boardField;
    }

    /**
     * Returns all chessboard tiles
     *
     * @return allTiles see {@link #allTiles}
     */
    public Tile[][] getAllTiles() {
        return allTiles;
    }

    /**
     * Returns a tile at the specified row and column
     *
     * @param row of the tile
     * @param col of the tile
     * @return allTiles[row][col] see {@link #allTiles}
     */
    public Tile getTileAt(int row, int col) {
        return allTiles[row][col];
    }

    /**
     * Count of all elements (rows * columns)
     *
     * @return ROWS * COLS
     */
    @Override
    public int getCount() {
        return ROWS * COLS;
    }

    /**
     * Returns a tile at the specified position
     *
     * @param position tile's position number
     * @return  allTilesByPosition[position] see {@link #allTilesByPosition}
     */
    @Override
    public Object getItem(int position) {
        return allTilesByPosition[position];
    }

    /**
     * Returns a tile's ID, at the specified position
     *
     * @param position tile's position number
     * @return tile's id
     */
    @Override
    public long getItemId(int position) {
        return allTilesByPosition[position].getId();
    }

    /**
     * Creates the chessboard, tile by tile
     *
     * @param position position number
     * @param convertView view to convert
     * @param parent parent view
     * @return created chessboard tile
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tileLayout = inflater.inflate(R.layout.tile, null);

        if (convertView == null) {
            Tile tile = (Tile) tileLayout.findViewById(R.id.single_tile);
            tile.initTile(position, mIsFlipped);
            tile.setChessPiece(mBoardField[tile.getCol()][tile.getRow()].toString());

            allTiles[tile.getRow()][tile.getCol()] = tile;
            allTilesByPosition[position] = tile;
        } else {
            tileLayout = convertView;
        }
        return tileLayout;
    }
}
