package Client.Pages;

import com.kt.AuthMessage;
import com.kt.Credentials;
import com.kt.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/23/13
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginPage extends ChessHeroPage {

    public JTextField usernameTextBox;
    public JPasswordField passwordTextBox;

    public  LoginPage(){
        this.setPageTitle("Login Page");
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

        this.usernameTextBox = new JTextField();
        this.passwordTextBox = new JPasswordField();

        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordTextBox.setHorizontalAlignment(SwingConstants.CENTER);
        usernameTextBox.setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        //Add Components
        menuPanel.add(usernameLabel);
        menuPanel.add(usernameTextBox);

        menuPanel.add(passwordLabel);
        menuPanel.add(passwordTextBox);

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
        mainPanel.add(loginButton, gridOpt);

        gridOpt.gridy = 5;
        gridOpt.weighty = 0.5;
        mainPanel.add(registerButton, gridOpt);


        this.setPagePanel(mainPanel);

        //this.add(mainPanel);

        //this.setContentPane(mainPanel);
        //this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add Listeners
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("You clicked the LOGIN button");
                handleLogin();
            }
        });


        registerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("You clicked the REGISTER button");
                handleRegister();
            }
        });
    }

    //Handle Buttons

    public void handleLogin(){
        //System.out.println(new String(this.passwordTextBox.getPassword()));

        holder.NavigateToPage(new LobbyPage());

        Credentials credentials = new Credentials(
                this.usernameTextBox.getText(),
                new String(this.passwordTextBox.getPassword())
        );

        AuthMessage authMsg = new AuthMessage(Message.TYPE_LOGIN, credentials);
        //holder.getConnection().writeMessage(authMsg);
    }

    public void handleRegister(){
        this.holder.NavigateToPage(new RegisterPage());
    }
}