package com.chesshero.ui.chessboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chesshero.R;
import com.kt.game.Position;

/**
 * Created by Vasil on 6.12.2014 г..
 * Represents a chessboard tile with all its properties
 */
public final class Tile extends ImageView {

    /**
     * Tile's image resource id
     */
    private int mImageResourceId;

    /**
     * Tile's column up on the grid
     */
    private int mCol;

    /**
     * Tile's row up on the grid
     */
    private int mRow;

    /**
     * Column + Row position
     */
    private Position mPosition;

    /**
     * True if tile's background is black, else false
     */
    private boolean mIsBlackBackground = false;

    /**
     * True if the user is playing with black chess piece set, else false
     */
    private boolean mIsFlipped = false;

    /**
     * True if the tile is available for next move, else false
     */
    private boolean mIsAvailableMove = false;

    /**
     * Default constructor calling ImageView's constructor
     *
     * @param context context
     * @param attrs attrs
     */
    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets tile column, row and background
     *
     * @param position an integer used to set tile's column and row
     * @param isFlipped see {@link #mIsFlipped}
     */
    public void initTile(int position, boolean isFlipped) {
        mIsFlipped = isFlipped;

        //set row, column and position
        setRow(position);
        setCol(position);
        mPosition = Position.positionFromBoardPosition(this.toString());

        //set background
        if ((mCol + mRow) % 2 == 0) {
            setBackgroundResource(R.drawable.transperant_white_cube);
        } else {
            setBackgroundResource(R.drawable.transperant_black_cube);
            mIsBlackBackground = true;
        }

        setScaleX(0.95f);
        setScaleY(0.95f);
    }

    /**
     * Places a chess pieces on the tile
     *
     * @param chessPieceCode chess piece code
     */
    public void setChessPiece(String chessPieceCode) {

        char piece = chessPieceCode.charAt(1);

        // change code letters
        // in order to flip black and white chess pieces
        // because in my implementation element[0][0] is up-left,
        // and on the server element[0][0] is down-left
        if (!mIsFlipped) {
            if (piece > 64 && piece < 91) piece += 32;
            else piece -= 32;
        }

        switch (piece) {
            case '-':
                mImageResourceId = 0;
                break;
            case 'P':
                mImageResourceId = R.drawable.white_pawn;
                break;
            case 'p':
                mImageResourceId = R.drawable.black_pawn;
                break;
            case 'K':
                mImageResourceId = mIsFlipped ? R.drawable.white_queen : R.drawable.white_king;
                break;
            case 'k':
                mImageResourceId = mIsFlipped ? R.drawable.black_queen : R.drawable.black_king;
                break;
            case 'B':
                mImageResourceId = R.drawable.white_bishop;
                break;
            case 'b':
                mImageResourceId = R.drawable.black_bishop;
                break;
            case 'R':
                mImageResourceId = R.drawable.white_rook;
                break;
            case 'r':
                mImageResourceId = R.drawable.black_rook;
                break;
            case 'Q':
                mImageResourceId = mIsFlipped ? R.drawable.white_king : R.drawable.white_queen;
                break;
            case 'q':
                mImageResourceId = mIsFlipped ? R.drawable.black_king : R.drawable.black_queen;
                break;
            case 'N':
                mImageResourceId = R.drawable.white_knight;
                break;
            case 'n':
                mImageResourceId = R.drawable.black_knight;
                break;
        }
        setImageResource(mImageResourceId);
        // if the chess piece belongs to the user - make it available move
        if (isMine()) {
            mIsAvailableMove = true;
        }
    }

    /**
     * Getter of the column parameter
     *
     * @return mCol see {@link #mCol}
     */
    public int getCol() {
        return mCol;
    }

    /**
     * Setter for the column parameter
     *
     * @param position an integer used to set tile's column
     */
    public void setCol(int position) {
        mCol = position % 8;
    }

    /**
     * Getter of the row parameter
     *
     * @return mRow see {@link #mRow}
     */
    public int getRow() {
        return mRow;
    }

    /**
     * Setter for the row parameter
     *
     * @param position an integer used to set tile's column
     */
    public void setRow(int position) {
        mRow = position / 8;
    }

    /**
     * Getter of the position parameter
     *
     * @return mPosition see {@link #mPosition}
     */
    public Position getPosition() {
        return mPosition;
    }

    /**
     * Setter for the available parameter
     *
     * @param isAvailable see {@link #mIsAvailableMove}
     */
    public void setAvailable(boolean isAvailable) {
        mIsAvailableMove = isAvailable;
    }

    /**
     * Getter of the available parameter
     *
     * @return mIsAvailableMove see {@link #mIsAvailableMove}
     */
    public boolean isAvailable() {
        return mIsAvailableMove;
    }

    /**
     * Sets an image resource
     *
     * @param imageId id of the image resource to be set
     */
    public void setTileImageId(int imageId) {
        mImageResourceId = imageId;
        setImageResource(mImageResourceId);
    }

    /**
     * Getter of the image resource id parameter
     *
     * @return mImageResourceId see {@link #mImageResourceId}
     */
    public int getTileImageId() {
        return mImageResourceId;
    }

    /**
     * See {@link #isWhiteFigure()}
     * See {@link #isBlackFigure()}
     * See {@link #isEmpty()}
     *
     * @return true if the tile's chess piece belongs to the user, else false
     */
    public boolean isMine() {
        return !mIsFlipped && isWhiteFigure() || mIsFlipped && isBlackFigure();
    }

    /**
     * See {@link #isMine()}
     * See {@link #isEmpty()}
     *
     * @return true if the tile is not empty and not mine, else false
     */
    public boolean isOponent() {
        return !isEmpty() && !isMine();
    }

    /**
     * Checks if the tile is empty or there is a chess piece placed in it
     *
     * @return true if the tile is empty, else false
     */
    public boolean isEmpty() {
        return mImageResourceId == 0;
    }

    /**
     * First checks if the tile is not empty
     * Then, if it not empty, it is checks if its chess piece is black
     *
     * @return true if the tile is not empty and has black chess piece, else false
     */
    public boolean isBlackFigure() {
        if (isEmpty()) return false;

        switch (mImageResourceId) {
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

    /**
     * First checks if the tile is not empty
     * Then, if it not empty, it is checks if its chess piece is white
     *
     * @return true if the tile is not empty and has white chess piece, else false
     */
    public boolean isWhiteFigure() {
        if (isEmpty()) return false;

        switch (mImageResourceId) {
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

    /**
     * Changes the background image with a highlighted one
     */
    public void applyHighlight() {
        if (mIsBlackBackground) {
            setBackgroundResource(R.drawable.transperant_black_cube_highlighted);
        } else {
            setBackgroundResource(R.drawable.transperant_white_cube_highlighted);
        }
    }

    /**
     * Restores the original background image
     */
    public void removeHighlight() {
        if (mIsBlackBackground) {
            setBackgroundResource(R.drawable.transperant_black_cube);
        } else {
            setBackgroundResource(R.drawable.transperant_white_cube);
        }
    }

    /**
     * Transforms column integer index to a character one
     *
     * @return A-H character string
     */
    public String getColumnString() {
        int col = mCol;
        if (mIsFlipped) {
            col = 7 - col;
        }
        switch (col) {
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

    /**
     * Parses row integer index to string
     *
     * @return 1-8 character string
     */
    public String getRowString() {
        // To get the actual mRow, add 1 since 'mRow' is 0 indexed.
        int row = mRow + 1;
        // handle straight case
        if (!mIsFlipped) {
            row = 9 - row;
        }
        return String.valueOf(row);
    }

    /**
     * See {@link #getColumnString()}
     * See {@link #getRowString()}
     *
     * @return column + row string
     */
    public String toString() {
        return getColumnString() + getRowString();
    }

    /**
     * Keeps the tile in a cubic shape, upon scale
     *
     * @param widthMeasureSpec width measure
     * @param heightMeasureSpec height measure
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}