package Client.Game;

import Client.Game.ChessPieces.ChessPiece;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardField {
    private BoardPosition fieldPosition = null;
    private ChessPiece occupyingPiece = null;
    private ChessColor fieldColor = null;

    public BoardPosition getFieldPosition() {
        return fieldPosition;
    }

    public ChessColor getFieldColor() {
        return fieldColor;
    }

    public ChessPiece getOccupyingPiece() {
        return occupyingPiece;
    }

    public void setOccupyingPiece(ChessPiece occupyingPiece) {
        this.occupyingPiece = occupyingPiece;
    }

    public BoardField(BoardPosition fieldPosition, ChessColor fieldColor, ChessPiece occupyingPiece){
        this(fieldPosition, fieldColor);
        this.occupyingPiece = occupyingPiece;

    }

    public BoardField(BoardPosition fieldPosition, ChessColor fieldColor){
        this.fieldPosition = fieldPosition;
        this.fieldColor = fieldColor;

    }

}
