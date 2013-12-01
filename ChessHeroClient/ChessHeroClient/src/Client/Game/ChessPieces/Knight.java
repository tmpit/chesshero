package Client.Game.ChessPieces;

import Client.Game.BoardPosition;
import Client.Game.GamePlayer;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/1/13
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Knight extends ChessPiece {

    public Knight(BoardPosition occupiedPosition, GamePlayer owner, boolean isTaken){
        super(occupiedPosition, owner,isTaken);
        this.movementSet = new ChessPieceMovementSet();   //Should be different for diff Pieces
        this.type = ChessPieceType.Knight;
    }
}
