import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaffStockDatabase
{
    private JTable DatabaseTable;
    private JTextField CodeInput;
    private JButton RestockButton;
    private JScrollPane ScrollPane;
    private JButton BackButton;
    private JPanel MainPanel;
    private JButton UpdateDBButton;
    private String[] ColumnNames = {"Code","Name", "Price", "Quantity"};
    private String[] Items = new String[4];
    private String[][] TableArray = new String[0][4];

    public StaffStockDatabase()
    {
        initialise();
    }

    public void initialise()
    {
        JFrame frame = new JFrame("StaffStockDatabase");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        DefaultTableModel TModel = (DefaultTableModel) DatabaseTable.getModel();

        String filename = "Test Write.txt";
        InputStream is = FileStream(filename);
        ReadFile(is, TModel);

        UpdateDBButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String data = "";
                        for(int count = 0; count < TableArray.length;)
                        {
                            data = data + DatabaseTable.getValueAt(count,0) + addSpace(3);
                            data = data + DatabaseTable.getValueAt(count, 1) + addSpace(20 - TableArray[count][1].length());
                            data = data + DatabaseTable.getValueAt(count,2) + addSpace(3);
                            data = data + DatabaseTable.getValueAt(count,3);
                            if(data.length() < 42)
                            {
                                data = data + addSpace(42-data.length());
                            }
                            //String data = TableArray[count][0] + addSpace(4);
                            //data = data + TableArray[count][1] + addSpace(23 - TableArray[count][1].length());
                            //data = data + TableArray[count][2] + addSpace(3);
                            //data = data + TableArray[count][3];

                            data = data + "\n";
                            count++;
                        }
                        FileWrite(data);
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
    void ReadFile(InputStream is, DefaultTableModel Tmodel) {
        System.out.println("Building DB Array");
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader))
        {
            //File MyFile = new File("Stock Database.txt");
            //Scanner reader = new Scanner(MyFile);
            String line;
            int count = 0;


            while ((line = reader.readLine()) != null)//.hasNextLine())
            {
                //line = reader.nextLine();
                TableArray = Array2DResize(TableArray);

                //Item Code
                TableArray[count][0] = Items[0] = line.substring(0, 5);
                //Item Name
                TableArray[count][1] = Items[1] = line.substring(9, 29);
                //Item Price
                TableArray[count][2] = Items[2] = line.substring(30, 35);
                //Item Quantity
                TableArray[count][3] = Items[3] = line.substring(39, 42);

                Tmodel.addRow(Items);
                count++;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Not Found");
            e.printStackTrace();
        }
    }

    void FileWrite(String data)
    {
        System.out.println("Writing to Database");
        try {
            Files.write(Paths.get("resources/Test Write.txt"), data.getBytes());
        } catch (IOException e) {
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

    private void createUIComponents() {
        DatabaseTable = new JTable(new DefaultTableModel(ColumnNames, 0));
    }

    private String addSpace(int spaces)
    {
        String space = "";

        for(int count = 0; count <= spaces;)
        {
            space = space + " ";
            count++;
        }
        return space;
    }
}
