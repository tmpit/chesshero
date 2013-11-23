package Client;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/23/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChessHeroPage extends JPanel{
    public static final String MAIN_TITLE = "Chess Hero";

    public ClientMain holder = null;
    private JPanel pagePanel = null;
    private String pageTitle = "";

//    public JFrame getHolder() {
//        return this.holder;
//    }
//
//
//    protected void setHolder(JFrame holder) {
//        this.holder = holder;
//    }

    public ChessHeroPage(){

    }

    public JPanel getPagePanel() {
        return pagePanel;
    }

    protected void setPagePanel(JPanel pagePanel) {
        this.pagePanel = pagePanel;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    protected void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

}
