import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class KioskUI extends JFrame
{
    private JPanel MainPanel;
    private JTable ItemsTable;
    private JTextField CodeInput;
    private JButton CodeSubmit;
    private JScrollPane ScrollPane;
    private JTextArea TotalCost;
    private JPanel RecieptPanel;
    private JButton CashButton;
    private JButton CardButton;
    private JTextArea RecieptText;
    private String currentCode;
    private String[][] dataArray = new String[0][4];
    //private String[] ColumnNames = {"Item Name", "Price"};
    private String[][] currentItems = new String[0][2];

    public KioskUI()
    {

        initialise();
    }

    public static void main(String[] args)
    {
        KioskUI kiosk = new KioskUI();
    }

    public void initialise()
    {
        //JFrame frame = new JFrame("KioskUI");
        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);


        String filename = "Stock Database.txt";
        InputStream is = FileStream(filename);
        ReadFile(is);

        CodeSubmit.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        System.out.println("Submitting Code");
                        currentCode = CodeInput.getText();
                        for(int count = 0; count < dataArray.length;)
                        {

                            if(dataArray[count][0].equals( currentCode))
                            {
                                currentItems = Array2DResize(currentItems);
                                currentItems[currentItems.length - 1][0] = dataArray[count][1];
                                currentItems[currentItems.length - 1][1] = dataArray[count][2];
                            }
                            count++;
                        }
                        //tblItems = new JTable(currentItems.length - 1, 2);


                        ItemsTable = createTable(currentItems);

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

    //Reads in the text file and adds them to the array
    void ReadFile(InputStream is) {
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
                dataArray = Array2DResize(dataArray);

                //Item Code
                dataArray[rcount][0] = line.substring(0, 5);
                //Item Name
                dataArray[rcount][1] = line.substring(9, 29);
                //Item Price
                dataArray[rcount][2] = line.substring(30, 35);
                //Item Quantity
                dataArray[rcount][3] = line.substring(39, 41);
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

    public static JTable createTable(String[][] itemsArray)
    {
        String[] columnNames = {"Item Name", "Price"};
        String[][] data = itemsArray;
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);

        return table;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}


