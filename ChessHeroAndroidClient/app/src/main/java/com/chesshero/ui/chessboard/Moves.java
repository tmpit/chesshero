package com.chesshero.ui.chessboard;

import com.chesshero.R;

import java.util.ArrayList;

/**
 * Created by lmn7 on 14.12.2014 г..
 */
public class Moves {

    private static final int BOARD_MIN = 0;

    private static final int BOARD_MAX = 7;

    private static final int PAWN_FIRST_ROW = 6;

    private static ArrayList<Tile> mPreviousHighlightedTiles = new ArrayList<Tile>();

    private static Tile[][] mAllTiles;

    private static Tile mCurrentTile;

    private static int mCurrentRow;

    private static int mCurrentCol;

    public Moves(Tile[][] allTiles) {
        mAllTiles = allTiles;
    }

    /**
     * Does magic!
     *
     * @param tile
     */
    private void doMagic(Tile tile) {
        if (tile.isMine()) {
            return;
        }
        tile.setAvailable(true);
        tile.applyHighlight();
        mPreviousHighlightedTiles.add(tile);
    }

    private void pawnMoves() {
        //first move rule
        if (mCurrentRow == PAWN_FIRST_ROW) {
            doMagic(mAllTiles[mCurrentRow - 2][mCurrentCol]);
        }
        if (mCurrentRow > BOARD_MIN) {
            doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol]);
        }
    }

    private void knightMoves() {
        //close left side
        if (mCurrentCol - 1 >= BOARD_MIN) {
            if (mCurrentRow - 2 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow - 2][mCurrentCol - 1]);
            }
            if (mCurrentRow + 2 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow + 2][mCurrentCol - 1]);
            }
        }
        //far left side
        if (mCurrentCol - 2 >= BOARD_MIN) {
            if (mCurrentRow - 1 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol - 2]);
            }
            if (mCurrentRow + 1 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow + 1][mCurrentCol - 2]);
            }
        }
        //close right side
        if (mCurrentCol + 1 <= BOARD_MAX) {
            if (mCurrentRow - 2 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow - 2][mCurrentCol + 1]);
            }
            if (mCurrentRow + 2 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow + 2][mCurrentCol + 1]);
            }
        }
        //far right side
        if (mCurrentCol + 2 <= BOARD_MAX) {
            if (mCurrentRow - 1 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol + 2]);
            }
            if (mCurrentRow + 1 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow + 1][mCurrentCol + 2]);
            }
        }
    }

    private void kingMoves() {
        // side neighbours
        if (mCurrentCol - 1 >= BOARD_MIN) {
            doMagic(mAllTiles[mCurrentRow][mCurrentCol - 1]);
        }
        if (mCurrentCol + 1 <= BOARD_MAX) {
            doMagic(mAllTiles[mCurrentRow][mCurrentCol + 1]);
        }
        if (mCurrentRow - 1 >= BOARD_MIN) {
            // side neighbour
            doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol]);
            // diagonal neighbours
            if (mCurrentCol - 1 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol - 1]);
            }
            if (mCurrentCol + 1 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow - 1][mCurrentCol + 1]);
            }
        }
        if (mCurrentRow + 1 <= BOARD_MAX) {
            // side neighbour
            doMagic(mAllTiles[mCurrentRow + 1][mCurrentCol]);
            // diagonal neighbours
            if (mCurrentCol - 1 >= BOARD_MIN) {
                doMagic(mAllTiles[mCurrentRow + 1][mCurrentCol - 1]);
            }
            if (mCurrentCol + 1 <= BOARD_MAX) {
                doMagic(mAllTiles[mCurrentRow + 1][mCurrentCol + 1]);
            }
        }
    }

    private void lineMoves() {

        int buffer;

        //move up
        buffer = mCurrentRow;
        while (--buffer >= BOARD_MIN) {
            if (mAllTiles[buffer][mCurrentCol].isMine()) break;
            doMagic(mAllTiles[buffer][mCurrentCol]);
            if (mAllTiles[buffer][mCurrentCol].isOponent()) break;
        }
        //move down
        buffer = mCurrentRow;
        while (++buffer <= BOARD_MAX) {
            if (mAllTiles[buffer][mCurrentCol].isMine()) break;
            doMagic(mAllTiles[buffer][mCurrentCol]);
            if (mAllTiles[buffer][mCurrentCol].isOponent()) break;
        }
        //move left
        buffer = mCurrentCol;
        while (--buffer >= BOARD_MIN) {
            if (mAllTiles[mCurrentRow][buffer].isMine()) break;
            doMagic(mAllTiles[mCurrentRow][buffer]);
            if (mAllTiles[mCurrentRow][buffer].isOponent()) break;
        }
        //move right
        buffer = mCurrentCol;
        while (++buffer <= BOARD_MAX) {
            if (mAllTiles[mCurrentRow][buffer].isMine()) break;
            doMagic(mAllTiles[mCurrentRow][buffer]);
            if (mAllTiles[mCurrentRow][buffer].isOponent()) break;
        }
    }

    private void diagonalMoves() {

        int colBuffer;
        int rowBuffer;

        //move up-left
        rowBuffer = mCurrentRow;
        colBuffer = mCurrentCol;
        while (--rowBuffer >= BOARD_MIN && --colBuffer >= BOARD_MIN) {
            if (mAllTiles[rowBuffer][colBuffer].isMine()) break;
            doMagic(mAllTiles[rowBuffer][colBuffer]);
            if (mAllTiles[rowBuffer][colBuffer].isOponent()) break;
        }
        //move up-right
        rowBuffer = mCurrentRow;
        colBuffer = mCurrentCol;
        while (--rowBuffer >= BOARD_MIN && ++colBuffer <= BOARD_MAX) {
            if (mAllTiles[rowBuffer][colBuffer].isMine()) break;
            doMagic(mAllTiles[rowBuffer][colBuffer]);
            if (mAllTiles[rowBuffer][colBuffer].isOponent()) break;
        }
        //move down-left
        rowBuffer = mCurrentRow;
        colBuffer = mCurrentCol;
        while (++rowBuffer <= BOARD_MAX && --colBuffer >= BOARD_MIN) {
            if (mAllTiles[rowBuffer][colBuffer].isMine()) break;
            doMagic(mAllTiles[rowBuffer][colBuffer]);
            if (mAllTiles[rowBuffer][colBuffer].isOponent()) break;
        }
        //move down-right
        rowBuffer = mCurrentRow;
        colBuffer = mCurrentCol;
        while (++rowBuffer <= BOARD_MAX && ++colBuffer <= BOARD_MAX) {
            if (mAllTiles[rowBuffer][colBuffer].isMine()) break;
            doMagic(mAllTiles[rowBuffer][colBuffer]);
            if (mAllTiles[rowBuffer][colBuffer].isOponent()) break;
        }
    }

    private void rookMoves() {
        lineMoves();
    }

    private void bishopMoves() {
        diagonalMoves();
    }

    private void queenMoves() {
        lineMoves();
        diagonalMoves();
    }

    public void clear() {
        for (Tile tile : mPreviousHighlightedTiles) {
            tile.setAvailable(false);
            tile.removeHighlight();
        }
    }

    public void apply(Tile currentTile) {
        mCurrentTile = currentTile;
        mCurrentRow = currentTile.getRow();
        mCurrentCol = currentTile.getCol();

        mPreviousHighlightedTiles.clear();

        mCurrentTile.applyHighlight();
        mPreviousHighlightedTiles.add(mCurrentTile);

        switch (mCurrentTile.getTileImageId()) {
            case R.drawable.white_pawn:
            case R.drawable.black_pawn:
                pawnMoves();
                break;
            case R.drawable.white_knight:
            case R.drawable.black_knight:
                knightMoves();
                break;
            case R.drawable.white_rook:
            case R.drawable.black_rook:
                rookMoves();
                break;
            case R.drawable.white_bishop:
            case R.drawable.black_bishop:
                bishopMoves();
                break;
            case R.drawable.white_king:
            case R.drawable.black_king:
                kingMoves();
                break;
            case R.drawable.white_queen:
            case R.drawable.black_queen:
                queenMoves();
                break;
        }
    }
}
