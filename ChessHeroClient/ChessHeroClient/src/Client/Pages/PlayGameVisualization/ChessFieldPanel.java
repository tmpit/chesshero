package Client.Pages.PlayGameVisualization;

import com.kt.game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 12/12/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessFieldPanel extends JLabel {
    private com.kt.game.Color fieldColor = null;
    private BufferedImage fieldImage = null;

    public com.kt.game.Color getFieldColor()
    {
        return this.fieldColor;
    }
    public BufferedImage getFiledImage()
    {
        return this.fieldImage;
    }
    public void setFiledImage(BufferedImage newImage)
    {
        this.fieldImage = newImage;
        if(fieldImage != null){
            this.setIcon(new ImageIcon(fieldImage));
        }
        else{
            this.setIcon(new ImageIcon());
        }
    }

    public Color getDisplayColor(){
//        return this.fieldColor == com.kt.game.Color.WHITE ? new Color(230,198,167): new Color(90,45,45);
        return this.fieldColor == com.kt.game.Color.WHITE ? new Color(207,178,150): new Color(99,50,50);
    }

    public ChessFieldPanel(com.kt.game.Color fieldColor,int size){
        this(fieldColor,size , null);
    }

    public ChessFieldPanel(com.kt.game.Color fieldColor ,int size, BufferedImage fieldImage){
        //super(new ImageIcon (fieldImage));
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setOpaque(true);
        if(fieldImage != null){
            this.setIcon(new ImageIcon(fieldImage));
        }
        else{
            this.setIcon(new ImageIcon());
        }
        this.setPreferredSize(new Dimension(size,size));
        this.setMaximumSize(new Dimension(size,size));
        this.setMinimumSize(new Dimension(size,size));
        this.fieldColor = fieldColor;
        this.fieldImage = fieldImage;
        this.setBackground(this.getDisplayColor());
        //this.setBounds(new Rectangle(0, 0, 50, 50));

    }
}