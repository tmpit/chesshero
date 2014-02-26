package Client.Pages.PlayGameVisualization;

import Client.Pages.PlayGamePage;
import com.kt.game.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/12/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessBoardTakenPiecesPanel extends JPanel {
    public ChessFieldPanel [] takenPiecesFields  = new ChessFieldPanel[16];
    private com.kt.game.Color playerColor;
    private int fieldSize;
    private Game game;

    public ChessBoardTakenPiecesPanel(Game game ,com.kt.game.Color playerColor, int fieldSize){
        this.playerColor = playerColor;
        this.fieldSize = fieldSize;
        this.game = game;

        initializeTakenPiecesFields();
        reDrawBoard();

    }

    public void setTakenPiecesFields(ArrayList<ChessPiece> takenPieces){
        //ArrayList<ChessPiece> newTakenPiecesList = new ArrayList<ChessPiece>(16);
        for (int i = 0; i < takenPieces.size(); i++){
            if(takenPieces.get(i) != null){
                BufferedImage bufferedImage = PlayGamePage.getTakenChessPieceImages().get(new String(
                       Byte.toString(takenPieces.get(i).getTag()) + takenPieces.get(i).getOwner().getColor()));
                this.takenPiecesFields[i].setIcon(new ImageIcon(bufferedImage));
            }
            else{
                this.takenPiecesFields[i].setIcon(new ImageIcon());
            }
        }
        updateUI();
    }

    private void initializeTakenPiecesFields() {
        for (int i = 0; i<takenPiecesFields.length; i++){
            takenPiecesFields[i] = new ChessFieldPanel(this.playerColor.Opposite,this.fieldSize);
        }
        if(this.playerColor == com.kt.game.Color.WHITE){
            ArrayList<ChessPiece> takenPieces = new ArrayList<ChessPiece>(this.game.getWhitePlayer().getChessPieceSet().getTakenPieces());

            if(takenPieces != null && takenPieces.size() > 0)
                setTakenPiecesFields(takenPieces);
        }
        else if(this.playerColor == com.kt.game.Color.BLACK){
            ArrayList<ChessPiece> takenPieces = new ArrayList<ChessPiece>(this.game.getBlackPlayer().getChessPieceSet().getTakenPieces());

            if(takenPieces != null && takenPieces.size() > 0)
                setTakenPiecesFields(takenPieces);
        }
    }

    public void updateTakenPieces(){
        reDrawBoard();
    }

    private void reDrawBoard(){
        clearBoard();
        initializeTakenPiecesFields();
        drawFields();
    }

    private void drawFields(){
        //Draw Fields
        for (int i = 0; i < this.takenPiecesFields.length;i++){

            this.add(takenPiecesFields[i]);
        }
    }
    private void clearBoard() {
        this.removeAll();
    }
}
