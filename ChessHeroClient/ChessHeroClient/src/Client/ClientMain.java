package Client;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/16/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientMain extends JFrame{

    public static final int HORIZONTAL_SIZE = 800;
    public static final int VERTICAL_SIZE = 600;
    //private JFrame

    public static ChessHeroPage currentPanel = null;

        public ClientMain() {
            //LoginPage testPage = new LoginPage();
            //testPage = new LoginPage();

            currentPanel = new LoginPage();
            this.setContentPane(currentPanel.getPagePanel());
            this.setTitle(currentPanel.getPageTitle());
            this.setPreferredSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
            this.setMaximumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
            this.setMinimumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
            this.setResizable(false);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            //LoginPage testPage1 = new LoginPage();
            this.setVisible(true);
        }



    public static void main(String args[]) {
        new ClientMain();

    }

}
