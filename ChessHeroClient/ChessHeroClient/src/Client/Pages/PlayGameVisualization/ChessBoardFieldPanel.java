package Client.Pages.PlayGameVisualization;

import com.kt.game.Position;

import javax.swing.*;
import javax.swing.border.Border;
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
    public Position fieldPosition = null;
    private boolean isSelected = false;
    private boolean isHighlighted = false;
    private boolean isPlayable = false;

    private Color defaultBackgroundColor;
    private static Border defaultBorder;

    public ChessBoardFieldPanel(com.kt.game.Color fieldColor, Position fieldPosition,int size){
        this(fieldColor, fieldPosition,size , null);
    }

    public ChessBoardFieldPanel(com.kt.game.Color fieldColor, Position fieldPosition,int size, BufferedImage fieldImage){
        super(fieldColor,size,fieldImage);
        if (defaultBorder == null) this.defaultBorder = this.getBorder();
        defaultBackgroundColor = this.getBackground();

        this.fieldPosition = fieldPosition;

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

    public boolean getIsPlayable()
    {
        return this.isPlayable;
    }

    public void setIsHighlighted (boolean isHighlighted)
    {
        this.isHighlighted = isHighlighted;
        if(this.isHighlighted == true)
        {
            this.setBorder(BorderFactory.createLineBorder(Color.CYAN,2));
        }
        else
        {
            this.setBorder(defaultBorder);
        }
    }

    public void setIsSelected (boolean isSelected)
    {
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

    public void setIsPlayable (boolean isPlayable)
    {
        this.isPlayable = isPlayable;
        if(this.isPlayable == true)
        {
            this.setBorder(BorderFactory.createLineBorder(Color.ORANGE,2));
//            this.setBackground(new Color(
//                    defaultBackgroundColor.getRed(),
//                    defaultBackgroundColor.getGreen(),
//                    defaultBackgroundColor.getBlue(),
//                    0.1f
//                    ));
        }
        else
        {
            this.setBorder(defaultBorder);
//            this.setBackground(defaultBackgroundColor);
        }
    }
}