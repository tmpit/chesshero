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
 * Date: 11/24/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateGamePage extends ChessHeroPage {

         public static GameSettings createGameSettings = null;//new CreateGameSettings();

        private JTextField  gameNameTextBox;

        // HELPER INNER CLASSES

        class GameSettings {
            public String GameName = "";
            public boolean IsWithWhite = true;
            public int TurnLimit = 0;
            public int TurnTimeLimitInSec = 0;
            public int GameTimeLimitInSec = 0;

            public GameSettings(){

            }
            public GameSettings(
                    String gameName,
                    boolean isWithWhite,
                    int turnLimit,
                    int turnTimeLimitInSec,
                    int gameTimeLimitInSec){
                this.GameName = gameName;
                this.IsWithWhite = isWithWhite;
                this.TurnLimit = turnLimit;
                this.TurnTimeLimitInSec = turnTimeLimitInSec;
                this.GameTimeLimitInSec = gameTimeLimitInSec;
            }

            @Override
            public String toString(){
                return this.GameName +"|"+
                        this.IsWithWhite +"|"+
                        this.TurnLimit + "|" +
                        this.TurnTimeLimitInSec + "|" +
                        this.GameTimeLimitInSec;
            }
        }



        public  CreateGamePage(){
            this.setPageTitle("Create Game Page");
            //Initialize Components
            JPanel mainPanel = new JPanel();
            JPanel menuPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            menuPanel.setLayout(new GridBagLayout());

            JLabel pageTitle = new JLabel(MAIN_TITLE);

            pageTitle.setHorizontalAlignment(JLabel.CENTER);
            pageTitle.setHorizontalTextPosition(JLabel.CENTER);
            pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

            JLabel pageSubTitle = new JLabel(this.getPageTitle());

            pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
            pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
            pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));

            JLabel gameNameLabel = new JLabel("Enter a name for your Game");

            this.gameNameTextBox = new JTextField();

            gameNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gameNameTextBox.setHorizontalAlignment(SwingConstants.CENTER);

            JButton lobbyPageButton = new JButton("Back To Lobby");
            JButton createGameButton = new JButton("Create Game");



            mainPanel.setLayout(new GridBagLayout());
            menuPanel.setLayout(new GridLayout(6,1));


            //Add Components
            menuPanel.add(gameNameLabel);
            menuPanel.add(gameNameTextBox);

            new GridBagConstraints();
            GridBagConstraints gridOpt = new GridBagConstraints();
            gridOpt.fill = GridBagConstraints.BOTH;
            gridOpt.insets = new Insets(20,200,20,200);
            gridOpt.gridx = 0;
            gridOpt.gridy = 0;
            //gridOpt.gridheight = 1;
            //gridOpt.ipady = 20;
            //gridOpt.ipadx = 100;
            gridOpt.weightx = 1;
            gridOpt.weighty = 0;
            mainPanel.add(pageTitle,gridOpt);

            gridOpt.insets = new Insets(0,200,20,200);
            gridOpt.gridy = 1;
            //gridOpt.weighty = 0;
            mainPanel.add(pageSubTitle, gridOpt);

            //gridOpt.fill = GridBagConstraints.BOTH;
            gridOpt.insets = new Insets(0,200,40,200);
            //gridOpt.ipadx = 10;
            gridOpt.gridx = 0;
            gridOpt.gridy = 2;
            //gridOpt.gridheight = 4;
            //gridOpt.weightx = 1;
            gridOpt.weighty = 6;
            //gridOpt.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(menuPanel, gridOpt);

            gridOpt.insets = new Insets(0,200,20,200);
            gridOpt.gridy = 4;
            gridOpt.weighty = 0.5;
            mainPanel.add(lobbyPageButton, gridOpt);

            gridOpt.gridy = 5;
            gridOpt.weighty = 0.5;
            mainPanel.add(createGameButton, gridOpt);


            this.setPagePanel(mainPanel);

            createGameSettings = new GameSettings();

            //Add Listeners

//            previousPageButton.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e)
//                {
//                    System.out.println("You clicked the previous page button");
//                    //HallOfFameEntry SelectedGame = gameList.get(table.getSelectedRow());
//                    handlePreviousPageButton();
//                }
//            });
//
//            nextPageButton.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e)
//                {
//                    System.out.println("You clicked the next page button");
//                    handleNextPageButton();
//                }
//            });
//
//            logoutButton.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println("You clicked the LOGOUT button");
//                    handleLogoutButton();
//                }
//            });
//
            lobbyPageButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    System.out.println("You clicked the Lobby page button");
                    handleLobbyPageButton();
                }
            });

            createGameButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e)
                {
                    System.out.println("You clicked the Create Game Page button");
                    handleCreateGameButton();
                }
            });
        }

    private void getSettings() {
        createGameSettings.GameName = gameNameTextBox.getText();
    }

    //Handle Buttons

    private void handleLobbyPageButton() {
        System.out.println("Entered Lobby button HANDLER");
        holder.NavigateToPage(new LobbyPage());
    }

    private void handleCreateGameButton() {
        System.out.println("Entered Create Game button HANDLER");
        getSettings();
        System.out.println(createGameSettings.toString());
        holder.NavigateToPage(new LobbyPage());
    }


//        private void handlePreviousPageButton() {
//            System.out.println("Should load previous results");
//        }
//
//        public void handleNextPageButton(){
//
//            //this.holder.NavigateToPage(new CreateGamePage());
//            System.out.println("Should load more results");
////        Credentials credentials = new Credentials(
////                this.usernameTextBox.getText(),
////                new String(this.passwordTextBox.getPassword())
////        );
////
////        AuthMessage authMsg = new AuthMessage(Message.ACTION_REGISTER, credentials);
//            //holder.getConnection().writeMessage(authMsg);
//        }
//
//        public void handleLogoutButton(){
//            this.holder.NavigateToPage(new LoginPage());
//        }
//
//
//        //HELPER METHODS
//        private Vector<Vector<String>> transformTableData(Vector<HallOfFameEntry> data){
//            Vector<Vector<String>> transformedData = new Vector<Vector<String>>();
//            for (HallOfFameEntry entry : data){
//                transformedData.add(new Vector<String>(Arrays.asList(entry.toString().split("\\|"))));
//            }
//            return  transformedData;
//        }
    }