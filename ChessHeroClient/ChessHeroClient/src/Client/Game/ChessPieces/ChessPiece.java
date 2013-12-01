package Client.Game.ChessPieces;

import Client.Game.BoardField;
import Client.Game.BoardPosition;
import Client.Game.ChessColor;
import Client.Game.GamePlayer;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChessPiece {

    public BoardPosition occupiedPosition = null;
    public GamePlayer owner = null;
    //public ChessColor color = null;
    public ChessPieceMovementSet movementSet = null;
    public boolean isTaken = false;
    public ChessPieceType type = null;

    private ChessPiece(){

    }

    public ChessPiece(GamePlayer owner, boolean isTaken){
        this(null, owner, isTaken);
    }

    public ChessPiece(BoardPosition occupiedPosition, GamePlayer owner){
        this(occupiedPosition, owner, false);
    }
    public ChessPiece(BoardPosition occupiedPosition, GamePlayer owner, boolean isTaken){

        this.occupiedPosition = occupiedPosition;
        this.owner = owner;
        //this.color = color;
        this.isTaken = isTaken;
    }
}
