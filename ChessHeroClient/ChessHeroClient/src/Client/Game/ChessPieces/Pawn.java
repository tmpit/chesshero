package Client.Game.ChessPieces;

import Client.Game.BoardField;
import Client.Game.BoardPosition;
import Client.Game.ChessColor;
import Client.Game.GamePlayer;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pawn extends ChessPiece {

    public Pawn(BoardPosition occupiedPosition, GamePlayer owner, boolean isTaken){
        super(occupiedPosition, owner,isTaken);
        this.movementSet = new ChessPieceMovementSet();   //Should be different for diff Pieces
        this.type = ChessPieceType.Pawn;
    }
}
