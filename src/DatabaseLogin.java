import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseLogin extends JFrame
{
    private JTextField UsernameTextInput;
    private JTextField PasswordTextInput;
    private JButton LoginButton;
    private JPanel MainPanel;

    public DatabaseLogin()
    {
        initialise();
    }

    public void initialise()
    {
        JFrame frame = new JFrame("DatabaseLogin");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        LoginButton.addActionListener(
                new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                if(UsernameTextInput.getText().equals("Admin") && PasswordTextInput.getText().equals("Password"))
                {
                    StaffStockDatabase StaffStockDB = new StaffStockDatabase();
                    frame.dispose();
                }
                else{
                    System.out.println("Incorrect Username or Password");
                    System.out.println(UsernameTextInput.getText() + " " + PasswordTextInput.getText());
                }

            }
        }
        );
    }
}
