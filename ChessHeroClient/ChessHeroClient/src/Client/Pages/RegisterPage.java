package Client.Pages;

import Client.Communication.Request;
import com.kt.*;
import com.kt.utils.SLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/23/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterPage extends ChessHeroPage {

    public JTextField usernameTextBox;
    public JPasswordField passwordTextBox;
    public JPasswordField confirmPasswordTextBox;

    public  RegisterPage(){
        super();
        this.setPageTitle("Register Page");
        //this.setSize(HORIZONTAL_SIZE, VERTICAL_SIZE);
        //Initialize Components
        JPanel mainPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));
        mainPanel.setLayout(new GridBagLayout());


        menuPanel.setLayout(new GridLayout(6,1));

        JLabel pageTitle = new JLabel(MAIN_TITLE);

        pageTitle.setHorizontalAlignment(JLabel.CENTER);
        pageTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

        JLabel pageSubTitle = new JLabel(this.getPageTitle());

        pageSubTitle.setHorizontalAlignment(JLabel.CENTER);
        pageSubTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageSubTitle.setFont(new Font("Serif", Font.BOLD, 32));

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");

        this.usernameTextBox = new JTextField();
        this.passwordTextBox = new JPasswordField();
        this.confirmPasswordTextBox = new JPasswordField();

        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordTextBox.setHorizontalAlignment(SwingConstants.CENTER);
        usernameTextBox.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPasswordTextBox.setHorizontalAlignment(SwingConstants.CENTER);

        JButton registerButton = new JButton("Submit Registration");
        JButton backButton = new JButton("Back");

        //Add Components
        menuPanel.add(usernameLabel);
        menuPanel.add(usernameTextBox);

        menuPanel.add(passwordLabel);
        menuPanel.add(passwordTextBox);
        menuPanel.add(confirmPasswordLabel);
        menuPanel.add(confirmPasswordTextBox);

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
        mainPanel.add(registerButton, gridOpt);

        gridOpt.gridy = 5;
        gridOpt.weighty = 0.5;
        mainPanel.add(backButton, gridOpt);

        this.setPagePanel(mainPanel);

        //this.add(mainPanel);

        //this.setContentPane(mainPanel);
        //this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add Listeners

        registerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("You clicked the REGISTER button");
                handleRegister();
            }
        });

        backButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("You clicked the Back button");
                handleBackButton();
            }
        });
    }

    //Handle Buttons

     public void handleRegister(){
         //System.out.println(new String(this.passwordTextBox.getPassword()));
         String username = this.usernameTextBox.getText();
         String password = new String(this.passwordTextBox.getPassword());
         String confirmPassword = new String(this.confirmPasswordTextBox.getPassword());
         Credentials credentials = null;
         if(username != null && password != null && confirmPassword != null && confirmPassword == password)
         {
             if(Credentials.isNameValid(username) && Credentials.isPassValid(password))
             {
                 credentials = new Credentials(username, password);
             }
         }

         if (credentials != null){
             AuthMessage authMsg = new AuthMessage(Message.TYPE_REGISTER, credentials);
             if (isConnected){
                 //this.getConnection().sendRequest(authMsg);
             }
         }
         //holder.getConnection().writeMessage(authMsg);
     }

    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response){
        //Logic to handle if the message is successful
        SLog.write("in request did complete in" + this.getPageTitle());
        this.getHolder().NavigateToPage(new LobbyPage());
    }

    public void handleBackButton(){
         this.getHolder().NavigateToPage(new LoginPage());
    }
}
