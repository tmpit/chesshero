package com.chesshero.ui.chessboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chesshero.R;

/**
 * Created by Vasil on 6.12.2014 г..
 */
public final class Tile extends ImageView {

    private static final int BLACK_BACKGROUND = R.drawable.black_background;

    private static final int WHITE_BACKGROUND = R.drawable.white_background;

    private static final int[] CHESS_PIECES = {
            // the 'dark' side of the board
            R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen,
            R.drawable.black_king, R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook,
            R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn,
            R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn,
            // the middle of the board
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            // the white side of the board
            R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn,
            R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn,
            R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen,
            R.drawable.white_king, R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook,
    };

    private int mCurrentTileImage;

    private int mCol;

    private int mRow;

    private boolean mIsFlipped = false;

    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Swaps kings and queens in the array,
     * because in inverted move they need to be swaped.
     */
    private void swapQeens() {
        CHESS_PIECES[3] = R.drawable.black_king;
        CHESS_PIECES[4] = R.drawable.black_queen;
        CHESS_PIECES[59] = R.drawable.white_king;
        CHESS_PIECES[60] = R.drawable.white_queen;
    }

    public int getCol() {
        return mCol;
    }

    public void setCol(int position) {
        mCol = position % 8;
    }

    public int getRow() {
        return mRow;
    }

    public void setRow(int position) {
        if (!mIsFlipped) {
            position = 63 - position;
        }
        mRow = position / 8;
    }

    public int getTileImage() {
        return mCurrentTileImage;
    }

    public void setTileImage(int imageId) {
        mCurrentTileImage = imageId;
        setImageResource(mCurrentTileImage);
    }

    public void initTile(int position, boolean isFlipped) {
        //set straight or inverted mode (black or white figures)
        if (isFlipped) {
            mIsFlipped = isFlipped;
            swapQeens();
        }

        //set row and coloumn
        setRow(position);
        setCol(position);

        //set background
        if ((mCol + mRow) % 2 == 0) {
            setBackgroundResource(BLACK_BACKGROUND);
        } else {
            setBackgroundResource(WHITE_BACKGROUND);
        }

        //set initial chess piece
        if (mIsFlipped) {
            position = 63 - position;
        }
        mCurrentTileImage = CHESS_PIECES[position];
        setImageResource(mCurrentTileImage);
    }

    public boolean isMine() {
        if (!isEmpty()) {
            if (isWhiteChessPiece() && !mIsFlipped) {
                return true;
            } else if (isBlackChessPiece() && mIsFlipped) {
                return true;
            }
        }
        return false;
    }

    public boolean isOponent() {
        return !isMine();
    }

    public boolean isEmpty() {
        return mCurrentTileImage == 0;
    }

    public boolean isBlackChessPiece() {
        if (isEmpty()) return false;

        switch (mCurrentTileImage) {
            case R.drawable.black_pawn:
                return true;
            case R.drawable.black_knight:
                return true;
            case R.drawable.black_rook:
                return true;
            case R.drawable.black_bishop:
                return true;
            case R.drawable.black_queen:
                return true;
            case R.drawable.black_king:
                return true;
        }
        return false;
    }

    public boolean isWhiteChessPiece() {
        if (isEmpty()) return false;

        switch (mCurrentTileImage) {
            case R.drawable.white_pawn:
                return true;
            case R.drawable.white_knight:
                return true;
            case R.drawable.white_rook:
                return true;
            case R.drawable.white_bishop:
                return true;
            case R.drawable.white_queen:
                return true;
            case R.drawable.white_king:
                return true;
        }
        return false;
    }

    public void applyHighlight() {
        setScaleType(ImageView.ScaleType.FIT_XY);
        setScaleX(0.9f);
        setScaleY(0.9f);
    }

    public void removeHighlight() {
        setScaleX(1f);
        setScaleY(1f);
    }

    // todo remove the following? or use it?
    public void handleTouch() {
        //todo
    }

    public String getColumnString() {

        switch (mCol) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            default:
                return null;
        }
    }

    public String getRowString() {
        // To get the actual mRow, add 1 since 'mRow' is 0 indexed.
        return String.valueOf(mRow + 1);
    }

    public String toString() {
        final String column = getColumnString();
        final String row = getRowString();
        return "<Tile " + column + " " + row + ">";
    }
}