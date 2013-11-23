package Client;

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
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterPage extends ChessHeroPage {

    public  RegisterPage(){
        this.setPageTitle("Register Page");
        //this.setSize(HORIZONTAL_SIZE, VERTICAL_SIZE);
        //Initialize Components
        JPanel mainPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));
        mainPanel.setLayout(new GridBagLayout());


        menuPanel.setLayout(new GridLayout(8,1));

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

        JTextField usernameTextBox = new JTextField();
        JTextField passwordTextBox = new JPasswordField();
        JTextField confirmPasswordTextBox = new JPasswordField();

        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordTextBox.setHorizontalAlignment(SwingConstants.CENTER);
        usernameTextBox.setHorizontalAlignment(SwingConstants.CENTER);

        //JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        //Add Components
        menuPanel.add(usernameLabel);
        menuPanel.add(usernameTextBox);

        menuPanel.add(passwordLabel);
        menuPanel.add(passwordTextBox);
        menuPanel.add(confirmPasswordTextBox);

        menuPanel.add(registerButton);
        new GridBagConstraints();
        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.insets = new Insets(40,200,20,200);
        gridOpt.gridx = 0;
        gridOpt.gridy = 0;
        //gridOpt.gridheight = 1;
        //gridOpt.ipady = 20;
        //gridOpt.ipadx = 100;
        gridOpt.weightx = 1;
        gridOpt.weighty = 0;
        //gridOpt.insets = new Insets(10,0,0,0);
        mainPanel.add(pageTitle,gridOpt);
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.insets = new Insets(0,200,0,200);
        //gridOpt.ipadx = 10;
        gridOpt.gridx = 0;
        gridOpt.gridy = 2;
        //gridOpt.gridheight = 2;
        gridOpt.weightx = 1;
        gridOpt.weighty = 1;
        //gridOpt.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(menuPanel, gridOpt);

        gridOpt.gridy = 1;
        gridOpt.weighty = 0;

        mainPanel.add(pageSubTitle,gridOpt);

        this.setPagePanel(mainPanel);

        //this.add(mainPanel);

        //this.setContentPane(mainPanel);
        //this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Handle Buttons

        registerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the REGISTER button");
                Credentials cred = new Credentials("uname","pass");
                AuthMessage authMsg = new AuthMessage(Message.ACTION_LOGIN, cred);
                holder.getConnection().writeMessage(authMsg);

            }
        });


    }
         public void handleRegister(){
             //Credentials cred = new Credentials(th.getText(),)
         }
}
