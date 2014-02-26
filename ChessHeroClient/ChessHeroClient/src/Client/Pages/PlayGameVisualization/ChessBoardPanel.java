package Client.Pages.PlayGameVisualization;

import Client.Pages.PlayGamePage;
import com.kt.game.BoardField;
import com.kt.game.ChessPiece;
import com.kt.game.Position;
import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/8/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessBoardPanel extends JPanel{

    public ChessBoardFieldPanel [][] chessBoardFields  = new ChessBoardFieldPanel[8][8];
    String[] colLabels = new String[]{"A","B","C","D","E","F","G","H"};
    String[] rowLabels = new String[]{"1","2","3","4","5","6","7","8"};
    private boolean isBoardReversed = false;

    public boolean getIsBoardReversed() {
        return this.isBoardReversed;
    }

    public void setIsBoardReversed(boolean isBoardReversed) {
        this.isBoardReversed = isBoardReversed;
    }

    public ChessBoardFieldPanel getField(Position position)
    {
        for (ChessBoardFieldPanel[] subList : this.chessBoardFields)
        {
            for (ChessBoardFieldPanel field : subList)
            {
                if (position.getX() == field.fieldPosition.getX() &&
                        position.getY() == field.fieldPosition.getY())
                {
                    return field;
                }
            }
        }
        return null;
    }

    public ChessBoardPanel(BoardField[][] board, int pieceSize){

        for (int i = 0; i<board.length;i++){
            for (int j = 0; j<board[i].length;j++){
                ChessBoardFieldPanel currentGameBoardFieldPanel = this.chessBoardFields[i][j];
                BoardField currentGameBoardField = board[i][j];
                Position currFieldPos = currentGameBoardField.getPosition();
                ChessPiece currOccupyingPiece = currentGameBoardField.getChessPiece();

                if (currOccupyingPiece != null){
                    this.chessBoardFields[currFieldPos.getY()][currFieldPos.getX()] =
                            new ChessBoardFieldPanel(
                                    currentGameBoardField.getColor(),
                                    currFieldPos,
                                    pieceSize,
                                    PlayGamePage.getChessPieceImages().get(new String(
                                           Byte.toString(currOccupyingPiece.getTag()) + currOccupyingPiece.getOwner().getColor()
                                    )));
                }
                else{
                    this.chessBoardFields[currFieldPos.getY()][currFieldPos.getX()] =
                            new ChessBoardFieldPanel(
                                    currentGameBoardField.getColor(),
                                    currFieldPos,
                                    pieceSize
                            );
                }
            }

        }

        this.redrawBoard();
    }

    public void updateBoard(BoardField[][] board){
        for (ChessBoardFieldPanel[] sublist : this.chessBoardFields){
            for (ChessBoardFieldPanel field : sublist){
                BoardField correspondingField =  board[field.fieldPosition.getX()][field.fieldPosition.getY()];
                ChessPiece currOccupyingPiece = correspondingField.getChessPiece();

                if (currOccupyingPiece != null){
                    field.setFiledImage(PlayGamePage.getChessPieceImages().get(new String(
                            Byte.toString(currOccupyingPiece.getTag()) + currOccupyingPiece.getOwner().getColor()
                    )));
                }
                else {
                    field.setFiledImage(null);
                }

            }
        }

    }

    public void redrawBoard() {
        this.setLayout(new GridBagLayout());
        clearBoard();
        drawBoardFields();
        drawLabels();
    }

    private void drawLabels(){
        GridBagConstraints GridOpt = new GridBagConstraints();
        GridOpt.weightx = 1;
        GridOpt.weighty = 1;
        GridOpt.gridwidth = 1;
        GridOpt.gridheight = 1;
        GridOpt.fill = GridBagConstraints.BOTH;

        //Draw Labels
        for (int i = 0; i < 10;i++){
            GridOpt.gridx = i;
            GridOpt.gridy = 0;
            JLabel labelFirstCols = new JLabel();
            JLabel labelSecondCols = new JLabel();
            JLabel labelFirstRows = new JLabel();
            JLabel labelSecondRows = new JLabel();
            labelFirstCols.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            labelSecondCols.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            labelFirstRows.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            labelSecondRows.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            if (i > 0 && i < 9) {
                if (isBoardReversed == false) {
                    labelFirstCols.setText(colLabels[i-1]);
                    labelSecondCols.setText(colLabels[i-1]);
                    labelFirstRows.setText(rowLabels[7-(i-1)]);
                    labelSecondRows.setText(rowLabels[7-(i-1)]);
                }
                else {
                    labelFirstCols.setText(colLabels[7-(i-1)]);
                    labelSecondCols.setText(colLabels[7-(i-1)]);
                    labelFirstRows.setText(rowLabels[i-1]);
                    labelSecondRows.setText(rowLabels[i-1]);
                }

            }
            this.add(labelFirstCols,GridOpt);
            GridOpt.gridy = 9;
            this.add(labelSecondCols,GridOpt);
            GridOpt.gridy = i;
            GridOpt.gridx = 0;
            this.add(labelFirstRows,GridOpt);
            GridOpt.gridx = 9;
            this.add(labelSecondRows,GridOpt);
        }
    }

    private void drawBoardFields(){
        GridBagConstraints GridOpt = new GridBagConstraints();
        GridOpt.weightx = 1;
        GridOpt.weighty = 1;
        GridOpt.gridwidth = 1;
        GridOpt.gridheight = 1;
        GridOpt.fill = GridBagConstraints.BOTH;

        for(ChessBoardFieldPanel[] chessBoardFieldRow : this.chessBoardFields){
            for (ChessBoardFieldPanel chessBoardField : chessBoardFieldRow){
                if (isBoardReversed == false) GridOpt.gridx = (chessBoardField.fieldPosition.getX()+1);
                else GridOpt.gridx = 9 - (chessBoardField.fieldPosition.getX()+1);
                if (isBoardReversed == false) GridOpt.gridy = 9 - (chessBoardField.fieldPosition.getY()+1);
                else GridOpt.gridy = (chessBoardField.fieldPosition.getY()+1);
                this.add(chessBoardField,GridOpt);
            }
        }
    }

    private void clearBoard() {
        this.removeAll();
    }
}
