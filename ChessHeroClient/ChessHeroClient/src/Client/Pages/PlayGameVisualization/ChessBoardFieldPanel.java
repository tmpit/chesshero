package Client.Pages.PlayGameVisualization;

import Client.Game.BoardPosition;
import Client.Game.ChessColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/8/13
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessBoardFieldPanel extends ChessFieldPanel {
//    public ChessColor fieldColor = null;
    public BoardPosition fieldPosition = null;
//    public BufferedImage fieldImage = null;

//    public Color getDisplayColor(){
//        return this.fieldColor == ChessColor.White ? new Color(230,198,167): new Color(90,45,45);
//    }

    public ChessBoardFieldPanel(ChessColor fieldColor, BoardPosition fieldPosition,int size){
        this(fieldColor, fieldPosition,size , null);
    }

    public ChessBoardFieldPanel(ChessColor fieldColor, BoardPosition fieldPosition,int size, BufferedImage fieldImage){
        super(fieldColor,size,fieldImage);
        //super(new ImageIcon (fieldImage));
        //this.setHorizontalAlignment(SwingConstants.CENTER);
        //this.setOpaque(true);
        //if(fieldImage != null){
        //    this.setIcon(new ImageIcon(fieldImage));
        //}
        //else{
        //    this.setIcon(new ImageIcon());
        //}
        //this.setPreferredSize(new Dimension(size,size));
        //this.fieldColor = fieldColor;
        this.fieldPosition = fieldPosition;
        //this.fieldImage = fieldImage;
        //this.setBackground(this.getDisplayColor());
        //this.setBounds(new Rectangle(0, 0, 50, 50));

    }
}