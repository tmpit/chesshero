package Client.Pages.PlayGameVisualization;

import com.kt.game.Position;
import com.kt.utils.SLog;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/8/13
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessBoardFieldPanel extends ChessFieldPanel {
//    public ChessColor fieldColor = null;
    public Position fieldPosition = null;
    private boolean isSelected = false;
    private boolean isHighlighted = false;

    private static Border defaultBorder;
//    public BufferedImage fieldImage = null;

//    public Color getDisplayColor(){
//        return this.fieldColor == ChessColor.White ? new Color(230,198,167): new Color(90,45,45);
//    }

    public ChessBoardFieldPanel(com.kt.game.Color fieldColor, Position fieldPosition,int size){
        this(fieldColor, fieldPosition,size , null);
    }

    public ChessBoardFieldPanel(com.kt.game.Color fieldColor, Position fieldPosition,int size, BufferedImage fieldImage){
        super(fieldColor,size,fieldImage);
        if (defaultBorder == null) this.defaultBorder = this.getBorder();

        this.fieldPosition = fieldPosition;

//        this.addMouseListener(this);
    }


    public void toggleIsHighlighted()
    {
        this.setIsHighlighted(!this.getIsHighlighted());
    }

    public void toggleIsSelected()
    {
        this.setIsSelected(!this.getIsSelected());
    }

    public boolean getIsHighlighted()
    {
        return this.isHighlighted;
    }

    public boolean getIsSelected()
    {
        return this.isSelected;
    }

    public void setIsHighlighted (boolean isHighlighted)
    {
        //firePropertyChange("isSelected",this.isSelected, isSelected);
        //this.propertyChange(new PropertyChangeEvent(this,"isSelected",this.isSelected, isSelected));
        this.isHighlighted = isHighlighted;
        if(this.isHighlighted == true)
        {
            this.setBorder(BorderFactory.createLineBorder(Color.ORANGE,2));
        }
        else
        {
            this.setBorder(defaultBorder);
        }
    }

    public void setIsSelected (boolean isSelected)
    {
        //firePropertyChange("isSelected",this.isSelected, isSelected);
        //this.propertyChange(new PropertyChangeEvent(this,"isSelected",this.isSelected, isSelected));
        this.isSelected = isSelected;
        if(this.isSelected == true)
        {
            this.setBorder(BorderFactory.createLineBorder(Color.MAGENTA,2));
        }
        else
        {
            this.setBorder(defaultBorder);
        }
    }
}