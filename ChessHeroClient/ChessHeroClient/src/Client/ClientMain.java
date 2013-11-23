package Client;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/16/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
import com.kt.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientMain extends JFrame implements ConnectionListener{

    private Connection con = null;

    public static final int HORIZONTAL_SIZE = 800;
    public static final int VERTICAL_SIZE = 600;
    //private JFrame

    public static ChessHeroPage currentPanel = null;

    public Connection getConnection(){
        if (con == null)    {
            try {
                this.con = Connection.getSingleton();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.con.addEventListener(this);
        }
        return con;

    }


    public ClientMain() {

        this.setPreferredSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setMaximumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setMinimumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        this.LoadPage(new LoginPage());


    }

    public void LoadPage(ChessHeroPage page){
        currentPanel = page;
        this.LoadCurrentPage();
    }

    public void LoadCurrentPage (){
        this.setContentPane(currentPanel.getPagePanel());
        this.setTitle(ChessHeroPage.MAIN_TITLE + currentPanel.getPageTitle());
        currentPanel.holder = this;
        this.setVisible(true);
    }


    public static void main(String args[]) {
        new ClientMain();

    }




    @Override
    public void messageRead(Message msg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void messageWritten(boolean result, Message msg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void socketDisconnected() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
