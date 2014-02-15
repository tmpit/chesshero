package Client;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/16/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
import Client.Communication.Connection;
import Client.Communication.ConnectionListener;
import Client.Pages.ChessHeroPage;
import Client.Pages.LoginPage;
import com.kt.game.Player;
import com.kt.utils.SLog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientMain extends JFrame {

    public static final int HORIZONTAL_SIZE = 1024;
    public static final int VERTICAL_SIZE = 720;

    public static ChessHeroPage currentPage = null;

    public static Player player;

    public ClientMain() {

        this.setPreferredSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setMaximumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setMinimumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.NavigateToPage(new LoginPage());
    }

    public void NavigateToPage(ChessHeroPage page){
        if (currentPage != null){
            currentPage.getConnection().removeEventListener(currentPage);
        }
        //this.con.addEventListener(page);
        currentPage = page;
        this.setContentPane(currentPage.getPagePanel());
        this.setTitle(ChessHeroPage.MAIN_TITLE + currentPage.getPageTitle());
        currentPage.setHolder(this);
        super.pack();
        super.validate();
        this.setVisible(true);
    }

    public static void main(String args[]) {
        new ClientMain();
    }
}