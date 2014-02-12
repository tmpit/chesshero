package Client.Pages;

import Client.Communication.Request;
import com.kt.*;
import com.kt.api.*;
import com.kt.api.Action;
import com.kt.game.Player;
import com.kt.utils.SLog;

import javax.swing.*;
import javax.swing.border.Border;
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

    public JLabel errorLabel;

    public Border defaultBorder;

    public  RegisterPage(){
        super();

        errorLabel = new JLabel(" ");

        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setHorizontalTextPosition(JLabel.CENTER);
        errorLabel.setFont(new Font("Serif", Font.BOLD, 12));
        errorLabel.setForeground(Color.red);

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

        gridOpt.gridy = 6;
        gridOpt.insets = new Insets(0,200,20,200);
        mainPanel.add(errorLabel, gridOpt);

        defaultBorder = usernameTextBox.getBorder();

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

         errorLabel.setText(" ");
         usernameTextBox.setBorder(defaultBorder);
         passwordTextBox.setBorder(defaultBorder);
         confirmPasswordTextBox.setBorder(defaultBorder);

         //System.out.println(new String(this.passwordTextBox.getPassword()));
         String username = this.usernameTextBox.getText();
         String password = new String(this.passwordTextBox.getPassword());
         String confirmPassword = new String(this.confirmPasswordTextBox.getPassword());

         if(username != null && password != null && confirmPassword != null && confirmPassword.equals(password))
         {
             if(Credentials.isNameValid(username) && Credentials.isPassValid(password))
             {
                 Request request = new Request(Action.REGISTER);
                 request.addParameter("username",username);
                 request.addParameter("password",password);

                 this.getConnection().sendRequest(request);
             }
         }
         if(username == null || !Credentials.isNameValid(username)){
             usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
         }
         if(password == null || !Credentials.isPassValid(password) || !confirmPassword.equals(password)){
             passwordTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
             confirmPasswordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
         }
     }

    @Override
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response){
        super.requestDidComplete(success, request, response);
        SLog.write("in request did complete in" + this.getPageTitle());

        int resultCode = (Integer)response.get("result");

        if (Result.OK == resultCode)
        {
            player = new Player((Integer)response.get("userid"), (String)response.get("username"));
            this.holder.NavigateToPage(new LobbyPage());
        }
        else if (Result.INVALID_NAME == resultCode)
        {
            usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            errorLabel.setText("Invalid username");
        }
        else if (Result.INVALID_PASS == resultCode)
        {
            passwordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            confirmPasswordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            errorLabel.setText("Invalid password");

        }
        else if (Result.BAD_USER == resultCode)
        {
            usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            passwordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            confirmPasswordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            errorLabel.setText("GET LOST!");

        }
        else if (Result.INVALID_CREDENTIALS == resultCode)
        {
            usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            passwordTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            confirmPasswordTextBox.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            errorLabel.setText("Invalid name or password");
        }
        else if (Result.ALREADY_LOGGEDIN == resultCode)
        {
            usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            passwordTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            errorLabel.setText("You are already logged in");
        }
        else if (Result.USER_EXISTS == resultCode)
        {
            usernameTextBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
            errorLabel.setText("There is already a user with that username");
        }
    }

    public void handleBackButton(){
         this.getHolder().NavigateToPage(new LoginPage());
    }
}
