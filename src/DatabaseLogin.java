import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class DatabaseLogin extends JFrame
{
    private JTextField UsernameTextInput;
    private JTextField PasswordTextInput;
    private JButton LoginButton;
    private JPanel MainPanel;
    String[][] loginArray = new String[0][2];

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

        String filename = "Login Credentials.txt";
        InputStream is = FileStream(filename);
        ReadFile(is);

        LoginButton.addActionListener(
                new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                boolean valid = false;
                for(int count = 0; count < loginArray.length;)
                {
                    if (UsernameTextInput.getText().equals(substringBlankRemover(loginArray[count][0])) && PasswordTextInput.getText().equals(substringBlankRemover(loginArray[count][1]))) {
                        StaffStockDatabase StaffStockDB = new StaffStockDatabase();
                        frame.dispose();
                        valid = true;
                    }
                    count++;
                }
                if(!valid)
                {
                    JOptionPane.showMessageDialog(null, "Incorrect Username or password", "Invalid Credentials", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
        );
    }
    private InputStream FileStream(String fileName)
    {
        ClassLoader csLoader = getClass().getClassLoader();
        InputStream inStream = csLoader.getResourceAsStream(fileName);

        if(inStream == null)
        {
            throw  new IllegalArgumentException("File not found" +fileName);
        }
        else {
            return inStream;
        }

    }

    void ReadFile(InputStream is)
    {
        System.out.println("Building DB Array");
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader))
        {
            //File MyFile = new File("Stock Database.txt");
            //Scanner reader = new Scanner(MyFile);
            String line;
            int rcount = 0;


            while ((line = reader.readLine()) != null)//.hasNextLine())
            {
                //line = reader.nextLine();
                loginArray = Array2DResize(loginArray);

                //Item Code
                loginArray[rcount][0] = line.substring(0, 20);
                loginArray[rcount][1] = line.substring(25, 45);
                rcount++;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Not Found");
            e.printStackTrace();
        }
    }

    String[][] Array2DResize(String[][] currArray)
    {
        int length = currArray.length;
        String[][] newArray = new String[length +1][4];

        for(int count = 0; count < length;)
        {
            newArray[count][0] = currArray[count][0];
            newArray[count][1] = currArray[count][1];
            newArray[count][2] = currArray[count][2];
            newArray[count][3] = currArray[count][3];
            count++;
        }

        return newArray;
    }

    private String substringBlankRemover(String check)
    {
        String out = "";
        for(int count = 0; count < check.length();)
        {
            if(check.charAt(count) != ' ')
            {
                out = out + check.charAt(count);
            }
            count++;
        }
        return out;
    }
}
