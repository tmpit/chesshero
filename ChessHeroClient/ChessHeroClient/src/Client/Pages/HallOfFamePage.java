package Client.Pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/25/13
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class HallOfFamePage extends ChessHeroPage{

    public static final Vector<String> LOBBY_TABLE_COLUMNS = new Vector<String>(Arrays.asList(
            "ID#", "Game Name", "Created By")
    );

    public Vector<HallOfFameEntry> gameList = null;
    public JTable table = null;

    // HELPER INNER CLASSES

    class HallOfFameEntry {
        public String gameID = null;
        public String gameName = null;
        public String createdBy = null;

        public HallOfFameEntry(String gameID, String gameName, String createdBy){
            this.gameID = gameID;
            this.gameName = gameName;
            this.createdBy = createdBy;
        }

        @Override
        public String toString(){
            return this.gameID +"|"+ this.gameName +"|"+ this.createdBy;
        }
    }

    class HallOfFameTableModel extends DefaultTableModel {

        public HallOfFameTableModel(Object[][] tableData, Object[] colNames) {
            super(tableData, colNames);
        }

        public HallOfFameTableModel(Vector tableData, Vector colNames) {
            super(tableData, colNames);
        }

        @Override
        public boolean isCellEditable(int row, int column)      //override isCellEditable
        {
            return false;
        }
    }

    public  HallOfFamePage(){
        super();
        this.setPageTitle("Hall Of Fame Page");
        //this.setSize(HORIZONTAL_SIZE, VERTICAL_SIZE);
        //Initialize Components
        JPanel mainPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));
        mainPanel.setLayout(new GridBagLayout());
//
//
//        menuPanel.setLayout(new GridLayout(6,1));
//
        JLabel pageTitle = new JLabel(MAIN_TITLE);
//
        pageTitle.setHorizontalAlignment(JLabel.CENTER);
        pageTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

        JLabel pageSubTitle = new JLabel(this.getPageTitle());

        pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
        pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));
//
//        JLabel usernameLabel = new JLabel("Username");
//        JLabel passwordLabel = new JLabel("Password");
//
//        this.usernameTextBox = new JTextField();
//        this.passwordTextBox = new JPasswordField();
//
//        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
//        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        passwordTextBox.setHorizontalAlignment(SwingConstants.CENTER);
//        usernameTextBox.setHorizontalAlignment(SwingConstants.CENTER);
//
        JButton previousPageButton = new JButton("Previous Page");
        JButton nextPageButton = new JButton("Next Page");
        JButton lobbyPageButton = new JButton("Back To Lobby");
        JButton logoutButton = new JButton("Logout");

        table = new JTable();
        JScrollPane tableHolder = new JScrollPane(table);
//
//        //Add Components
//        menuPanel.add(usernameLabel);
//        menuPanel.add(usernameTextBox);
//
//        menuPanel.add(passwordLabel);
//        menuPanel.add(passwordTextBox);
//
        new GridBagConstraints();
        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.insets = new Insets(20,100,20,100);
        gridOpt.gridx = 0;
        gridOpt.gridy = 0;
        gridOpt.weightx = 2;
        gridOpt.weighty = 0;
        gridOpt.gridwidth = 2;
        mainPanel.add(pageTitle,gridOpt);

        gridOpt.insets = new Insets(0,100,20,100);
        gridOpt.gridy = 1;
        mainPanel.add(pageSubTitle, gridOpt);

        gridOpt.insets = new Insets(0,100,40,100);
        gridOpt.gridx = 0;
        gridOpt.gridy = 2;
        gridOpt.weighty = 6;
        mainPanel.add(tableHolder, gridOpt);


        gridOpt.insets = new Insets(0,100,20,10);
        gridOpt.gridy = 5;
        gridOpt.weighty = 0.5;
        gridOpt.gridwidth = 1;
        gridOpt.fill = GridBagConstraints.BOTH;
        mainPanel.add(previousPageButton, gridOpt);

        gridOpt.insets = new Insets(0,10,20,100);
        gridOpt.gridx = 1;
        mainPanel.add(nextPageButton, gridOpt);

        gridOpt.insets = new Insets(0,100,20,10);
        gridOpt.gridx = 0;
        gridOpt.gridy = 6;
        mainPanel.add(lobbyPageButton, gridOpt);

        gridOpt.insets = new Insets(0,10,20,100);
        gridOpt.gridx = 1;
        mainPanel.add(logoutButton, gridOpt);


        this.setPagePanel(mainPanel);

        loadHallOfFame();


        //Add Listeners

        previousPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the previous page button");
                //HallOfFameEntry SelectedGame = gameList.get(table.getSelectedRow());
                handlePreviousPageButton();
            }
        });

        nextPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the next page button");
                handleNextPageButton();
            }
        });

        logoutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("You clicked the LOGOUT button");
                handleLogoutButton();
            }
        });

        lobbyPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the Lobby page button");
                handleLobbyPageButton();
            }
        });
    }



    //Handle Buttons
    private void loadHallOfFame() {
        gameList = new Vector<HallOfFameEntry>(Arrays.asList(
                new HallOfFameEntry(
                        "867954","game1","user1"
                ),new HallOfFameEntry(
                "522532", "game2", "user2"
        ), new HallOfFameEntry(
                "4213532", "game3","user3"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        ),new HallOfFameEntry(
                "5135132", "game4","user4"
        )
        )
        );
        table.setModel(new HallOfFameTableModel(transformTableData(gameList), LOBBY_TABLE_COLUMNS));

    }

    private void handleLobbyPageButton() {
        System.out.println("Entered Lobby button HANDLER");
        holder.NavigateToPage(new LobbyPage());
    }


    private void handlePreviousPageButton() {
        System.out.println("Should load previous results");
    }

    public void handleNextPageButton(){

        //this.holder.NavigateToPage(new CreateGamePage());
        System.out.println("Should load more results");
//        Credentials credentials = new Credentials(
//                this.usernameTextBox.getText(),
//                new String(this.passwordTextBox.getPassword())
//        );
//
//        AuthMessage authMsg = new AuthMessage(Message.ACTION_REGISTER, credentials);
        //holder.getConnection().writeMessage(authMsg);
    }

    public void handleLogoutButton(){
        this.holder.NavigateToPage(new LoginPage());
    }


    //HELPER METHODS
    private Vector<Vector<String>> transformTableData(Vector<HallOfFameEntry> data){
        Vector<Vector<String>> transformedData = new Vector<Vector<String>>();
        for (HallOfFameEntry entry : data){
            transformedData.add(new Vector<String>(Arrays.asList(entry.toString().split("\\|"))));
        }
        return  transformedData;
    }
}