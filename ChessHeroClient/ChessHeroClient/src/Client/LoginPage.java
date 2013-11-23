package Client;

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




    public  LoginPage(){

        //this.setSize(HORIZONTAL_SIZE, VERTICAL_SIZE);
        //Initialize Components
        JPanel mainPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        //mainPanel.setLayout(new BoxLayout(mainPanel, FlowLayout.CENTER));
        mainPanel.setLayout(new GridBagLayout());


        menuPanel.setLayout(new GridLayout(7,1));

        JLabel pageTitle = new JLabel("Chess Hero");
        pageTitle.setHorizontalAlignment(JLabel.CENTER);
        pageTitle.setHorizontalTextPosition(JLabel.CENTER);
        pageTitle.setFont(new Font("Serif", Font.BOLD, 48));

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");

        JTextField usernameTextBox = new JTextField();
        JTextField passwordTextBox = new JTextField();

        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        //Add Components
        menuPanel.add(usernameLabel);
        menuPanel.add(usernameTextBox);

        menuPanel.add(passwordLabel);
        menuPanel.add(passwordTextBox);

        menuPanel.add(loginButton);
        menuPanel.add(registerButton);
        new GridBagConstraints();
        GridBagConstraints gridOpt = new GridBagConstraints();
        gridOpt.fill = GridBagConstraints.HORIZONTAL;
        gridOpt.gridx = 0;
        gridOpt.gridy = 0;
        gridOpt.gridheight = 1;
        //gridOpt.ipady = 20;
        //gridOpt.ipadx = 100;
        gridOpt.weightx = 1;
        gridOpt.weighty = 1;
        //gridOpt.insets = new Insets(10,0,0,0);

        mainPanel.add(pageTitle,gridOpt)  ;
        gridOpt.fill = GridBagConstraints.BOTH;
        gridOpt.insets = new Insets(0,200,0,200);
        //gridOpt.ipadx = 10;
        gridOpt.gridx = 0;
        gridOpt.gridy = 1;
        gridOpt.gridheight = 2;
        gridOpt.weightx = 1;
        gridOpt.weighty = 2;
        //gridOpt.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(menuPanel, gridOpt);

        this.setPagePanel(mainPanel);
        this.setPageTitle("Chess Hero");

        //this.add(mainPanel);

        //this.setContentPane(mainPanel);
        //this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Handle Buttons
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the LOGIN button");
            }
        });


        registerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                System.out.println("You clicked the REGISTER button");
            }
        });









        //pnlText.add(new TextField(10), BorderLayout.CENTER);
        //add(mainPanel, BorderLayout.WEST);
        //pnlGrid.setLayout(new GridLayout(2,3));
        //for (int i = 0; i < 6; i++)
        //    pnlGrid.add(new Button("" + (i + 1)));
        //add(pnlGrid, BorderLayout.EAST);
        //pnlButtons.add(new Button("Start"));
        //pnlButtons.add(new Button("Quit"));
        //add(pnlButtons, BorderLayout.SOUTH);
    }
}