package Client.Game;

import Client.Game.ChessPieces.*;
import com.kt.ChessHeroException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game {
    public static final int BoardLength = 8;

    public BoardField[][] board = null;

    public GamePlayer whitePlayer = null;
    public GamePlayer blackPlayer = null;

    private List<ChessPiece> activePieces = null;
    private List<ChessPiece> takenPieces = null;

    //Properties

//    public void addActivePiece(ChessPiece newPiece, GamePlayer owner){
//        newPiece.isTaken = false;
//        newPiece.owner = owner;
//        board[newPosition.row][newPosition.col].setOccupyingPiece(newPiece);
//        owner.addActivePiece(newPiece);
//        this.activePieces.add(newPiece);
//    }

    public GamePlayer getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(GamePlayer blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public GamePlayer getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(GamePlayer whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void addActivePiece(ChessPiece newPiece,BoardPosition newPosition){
        newPiece.isTaken = false;
        //newPiece.owner = owner;
        newPiece.occupiedPosition = newPosition;
        board[newPosition.row][newPosition.col].setOccupyingPiece(newPiece);
        newPiece.owner.addActivePiece(newPiece);
        this.activePieces.add(newPiece);
    }

    public void addTakenPiece(ChessPiece newPiece, GamePlayer newOwner){
        newPiece.isTaken = true;
        //newPiece.owner = null;
        newPiece.occupiedPosition = null;
        newOwner.addTakenPiece(newPiece);
        this.takenPieces.add(newPiece);
    }

    public List<ChessPiece> getActivePieces() {
        return activePieces;
    }

    public void addActivePieces(List<ChessPiece> activePieces) {
        if (activePieces != null){
            if(this.activePieces == null){
                this.activePieces = new ArrayList<ChessPiece>();
            }
            //this.activePieces = new ArrayList<ChessPiece>();
            for(int i = 0; i < activePieces.size(); i++){
                ChessPiece currentPiece = activePieces.get(i);
                if (currentPiece.occupiedPosition == null){
                    //Should Throw Exc
                    //throw new ChessHeroException(42);
                }
                else{
                    this.addActivePiece(
                            currentPiece,
                            currentPiece.occupiedPosition
                    );
                }
            }
        }
    }

    public void setActivePieces(List<ChessPiece> activePieces) {
        if (activePieces != null){
//            if(this.activePieces == null){
                this.activePieces = new ArrayList<ChessPiece>();
//            }
            //this.activePieces = new ArrayList<ChessPiece>();
            for(int i = 0; i < activePieces.size(); i++){
                ChessPiece currentPiece = activePieces.get(i);
                if (currentPiece.occupiedPosition == null){
                    //Should Throw Exc
                    //throw new ChessHeroException(42);
                }
                else{
                    this.addActivePiece(
                            currentPiece,
                            currentPiece.occupiedPosition
                    );
                }
            }
        }

        //this.activePieces = activePieces;
    }

    public List<ChessPiece> getTakenPieces() {
        return takenPieces;
    }

    public void setTakenPieces(List<ChessPiece> takenPieces, GamePlayer newOwner) {
        if (takenPieces != null){
//            if (this.takenPieces != null){
                this.takenPieces = new ArrayList<ChessPiece>();
//            }
            for(int i = 0; i < takenPieces.size(); i++){
                ChessPiece currentPiece = takenPieces.get(i);
//                if (currentPiece.owner != null){
                    this.addTakenPiece(currentPiece, newOwner);
//                }
//                currentPiece.owner.playerColor = this.playerColor;
//                currentPiece.isTaken = true;
//                currentPiece.occupiedPosition = null;
            }
        }
    }

    //public ArrayList<ChessPiece> whitePlayerActivePieces = null;//new ArrayList<ChessPiece>();
    //public ArrayList<ChessPiece> blackPlayerActivePieces = null;//new ArrayList<ChessPiece>();
    //public ArrayList<ChessPiece> whitePlayerTakenPieces = null;//new ArrayList<ChessPiece>();
    //public ArrayList<ChessPiece> blackPlayerTakenPieces = null;//new ArrayList<ChessPiece>();

    //public static final BoardField[][] =
    public Game(){
        this.board = this.generateEmptyBoard();
//        this.blackPlayer = new GamePlayer(ChessColor.Black);
//        this.whitePlayer = new GamePlayer(ChessColor.White);
//        this.startNewGame(this.getWhitePlayer(), this.getBlackPlayer());
    }

    //Generates empty board with black and white squares
    public BoardField[][] generateEmptyBoard(){
        BoardField[][] board = new BoardField[BoardLength][BoardLength];
        ChessColor fieldColor = ChessColor.White;
        for (int row = 0; row < BoardLength; row++){
            fieldColor = fieldColor.Opposite;
            for (int col = 0; col < BoardLength; col++){
                board[row][col] = new BoardField(new BoardPosition(row,col),fieldColor);
                fieldColor=fieldColor.Opposite;
            }
        }
        return  board;
    }

    //
    public void startNewGame(GamePlayer whitePlayer, GamePlayer blackPlayer){
        //this.board = this.generateEmptyBoard();
        this.populateBoard(whitePlayer, blackPlayer);
    }

    public static ArrayList<ChessPiece> generatePlayerPieceSet(GamePlayer owner){
        ArrayList<ChessPiece> playerPieceSet = new ArrayList<ChessPiece>();
        for (int i = 0; i< BoardLength; i++){
            playerPieceSet.add(new Pawn(null, owner, false));
        }

        //Different Pieces should be here
        for (int i = 0; i< BoardLength; i++){
            playerPieceSet.add(new Pawn(null, owner, false));
        }
        return playerPieceSet;
    }

    public void populateBoard(GamePlayer whitePlayer, GamePlayer blackPlayer){
        //ArrayList<ChessPiece> whitePlayerPieceSet = generatePlayerPieceSet(whitePlayer);
        //ArrayList<ChessPiece> blackPlayerPieceSet = generatePlayerPieceSet(blackPlayer);
        if (this.board != null){
            this.setActivePieces(getPlayerStartingPieceSet(whitePlayer));
            this.addActivePieces(getPlayerStartingPieceSet(blackPlayer));
        }

    }


    public List<ChessPiece> getPlayerStartingPieceSet(GamePlayer owner){
        int pawnRow;
        int otherRow;
        int[] pawnCols = new int[]{0,1,2,3,4,5,6,7};
        int[] rookCols = new int[]{0,7};
        int[] knightCols = new int[]{1,6};
        int[] bishopCols = new int[]{2,5};
        int queenCol;
        int kingCol;

        List<ChessPiece> playerStartingSet = new ArrayList<ChessPiece>(16);

        if (owner.getPlayerColor() != null){
            if (owner.getPlayerColor() == ChessColor.White){
                pawnRow = 1;
                otherRow = 0;
                queenCol = 3;
                kingCol = 4;
            }
            else if (owner.getPlayerColor() == ChessColor.Black){
                pawnRow = 6;
                otherRow = 7;
                queenCol = 3;
                kingCol = 4;
            }
            else{
                //should throw exc
                pawnRow = -1;
                otherRow = -1;
                queenCol = -1;
                kingCol = -1;
            }
            playerStartingSet.add(new King(new BoardPosition(otherRow,kingCol), owner ,false));
            playerStartingSet.add(new Queen(new BoardPosition(otherRow,queenCol), owner ,false));

            for (int piecePos : pawnCols){
                playerStartingSet.add(new Pawn(new BoardPosition(pawnRow,piecePos), owner ,false));
            }
            for (int piecePos : rookCols){
                playerStartingSet.add(new Rook(new BoardPosition(otherRow,piecePos), owner ,false));
            }
            for (int piecePos : knightCols){
                playerStartingSet.add(new Knight(new BoardPosition(otherRow,piecePos), owner ,false));
            }
            for (int piecePos : bishopCols){
                playerStartingSet.add(new Bishop(new BoardPosition(otherRow,piecePos), owner ,false));
            }
        }
        return  playerStartingSet;
    }

//    public List<ChessPiece> WHITE_PLAYER_STARTING_PIECE_SET = new ArrayList<ChessPiece>(Arrays.asList(
//            new Pawn(new BoardPosition(1,0), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,1), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,2), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,3), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,4), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,5), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,6), null ,false),     //pawns
//            new Pawn(new BoardPosition(1,7), null ,false),     //pawns
//
//            new Pawn(new BoardPosition(0,0), null ,false),     //Rook
//            new Pawn(new BoardPosition(0,1), null ,false),     //Knight
//            new Pawn(new BoardPosition(0,2), null ,false),     //Bishop
//            new Pawn(new BoardPosition(0,3), null ,false),     //Queen
//            new Pawn(new BoardPosition(0,4), null ,false),     //King
//            new Pawn(new BoardPosition(0,5), null ,false),     //Bishop
//            new Pawn(new BoardPosition(0,6), null ,false),     //Knight
//            new Pawn(new BoardPosition(0,7), null ,false)      //Rook
//    ));
//
//    public List<ChessPiece> BLACK_PLAYER_STARTING_PIECE_SET = new ArrayList<ChessPiece>(Arrays.asList(
//            new Pawn(new BoardPosition(6,0), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,1), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,2), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,3), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,4), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,5), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,6), null ,false),     //pawns
//            new Pawn(new BoardPosition(6,7), null ,false),     //pawns
//
//            new Pawn(new BoardPosition(7,0), null ,false),     //Rook
//            new Pawn(new BoardPosition(7,1), null ,false),     //Knight
//            new Pawn(new BoardPosition(7,2), null ,false),     //Bishop
//            new Pawn(new BoardPosition(7,4), null ,false),     //Queen
//            new Pawn(new BoardPosition(7,3), null ,false),     //King
//            new Pawn(new BoardPosition(7,5), null ,false),     //Bishop
//            new Pawn(new BoardPosition(7,6), null ,false),     //Knight
//            new Pawn(new BoardPosition(7,7), null ,false)      //Rook
//    ));
}
