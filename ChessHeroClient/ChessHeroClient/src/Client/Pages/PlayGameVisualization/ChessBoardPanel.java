package Client.Pages.PlayGameVisualization;

import Client.Pages.PlayGamePage;
import com.kt.game.BoardField;
import com.kt.game.ChessPiece;
import com.kt.game.Position;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/8/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessBoardPanel extends JPanel {

    ChessBoardFieldPanel [][] chessBoardFields  = new ChessBoardFieldPanel[8][8];
    String[] colLabels = new String[]{"A","B","C","D","E","F","G","H"};
    String[] rowLabels = new String[]{"1","2","3","4","5","6","7","8"};
    private boolean isBoardReversed = false;

    public boolean getIsBoardReversed() {
        return isBoardReversed;
    }

    public void setIsBoardReversed(boolean isBoardReversed) {
        this.isBoardReversed = isBoardReversed;
        this.redrawBoard();
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
                                    PlayGamePage.ChessPieceImages.get(new Pair<Byte, com.kt.game.Color>(
                                            currOccupyingPiece.getTag(), currOccupyingPiece.getOwner().getColor()
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

    private void redrawBoard() {
        this.setLayout(new GridBagLayout());
        clearBoard();
        drawBoardFields();
        drawLabels();
    }

//        private void drawBoardDownTop() {
//            //GridBagConstraints GridOpt = new GridBagConstraints();
//
//            this.setLayout(new GridLayout(10,10));
//
//            for(int row = 8; row >= -1; row--){
//                for (int col = -1; col <= 8; col++){
//                    if ((row == -1 || row == 8) || (col == -1 || col == 8)){
//                        JLabel label = new JLabel();
//                        label.setHorizontalAlignment(SwingConstants.CENTER);
//                        if ((col == -1 || col == 8) && (row != -1 && row != 8)){
//                            label.setText(rowLabels[row]);
//                            this.add(label);
//                        }
//                        else if ((row == -1 || row == 8) && (col != -1 && col != 8)){
//                            label.setText(colLabels[col]);
//                            this.add(label);
//                        }
//                        else {
//                            this.add(label);
//                        }
//                    }
//                    else {
//                        this.add(this.chessBoardFields[row][col]);
//
//                    }
//                }
//            }
//        }

//        private void drawBoardTopDown() {
//            //GridBagConstraints GridOpt = new GridBagConstraints();
////            GridOpt.gridwidth = 50;
////            GridOpt.gridheight = 50;
////            GridOpt.weightx = 1;
////            GridOpt.weighty = 1;
////            GridOpt.fill = GridBagConstraints.RELATIVE;
//
//
//            this.setLayout(new GridLayout(10,10));
//
//            for(int row = -1; row <= 8; row++){
//                for (int col = -1; col <= 8; col++){
////                    GridOpt.gridx = col;
////                    GridOpt.gridy = row;
//                    //this.add(this.chessBoardFields[row][col],GridOpt);
//                    if ((row == -1 || row == 8) || (col == -1 || col == 8)){
//                        JLabel label = new JLabel();
//                        label.setHorizontalTextPosition(SwingConstants.CENTER);
//
//                        if ((col == -1 || col == 8) && (row != -1 && row != 8)){
//                            label.setText(rowLabels[row]);
//                            this.add(label);
//                        }
//                        else if ((row == -1 || row == 8) && (col != -1 && col != 8)){
//                            label.setText(colLabels[col]);
//                            this.add(label);
//                        }
//                        else {
//                            this.add(label);
//                        }
//                    }
//                    else {
//                        this.add(this.chessBoardFields[row][col]);
//
//                    }
//                }
//            }
//            //
////                    this.add(this.chessBoardFields[i][j],GridOpt);
//        }

    private void drawLabels(){
        GridBagConstraints GridOpt = new GridBagConstraints();
        GridOpt.weightx = 0.5;
        GridOpt.weighty = 0.5;
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
