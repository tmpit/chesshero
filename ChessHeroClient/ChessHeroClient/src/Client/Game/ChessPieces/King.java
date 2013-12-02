package Client.Game.ChessPieces;

import Client.Game.BoardPosition;
import Client.Game.GamePlayer;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class King extends ChessPiece {

    public King(BoardPosition occupiedPosition, GamePlayer owner, boolean isTaken){
        super(occupiedPosition, owner,isTaken);
        this.movementSet = new ChessPieceMovementSet();   //Should be different for diff Pieces
        this.type = ChessPieceType.King;
    }
}
