import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class KioskUI extends JFrame {
    private JPanel MainPanel;
    private JTable ItemsTable;
    private JTextField CodeInput;
    private JButton CodeSubmit;
    private JScrollPane ScrollPane;
    private JTextArea TotalCost;
    private JButton CashButton;
    private JButton CardButton;
    private JTextArea RecieptText;
    private JButton StockDatabaseButton;
    private JButton RemoveItemButton;
    private JPanel RecieptPanel;

    //The current item code
    private String currentCode;
    //An array of the database information
    private String[][] dataArray = new String[0][4];
    //The column names for the table/table model
    private String[] ColumnNames = {"Item Name", "Price"};
    //array to hold the name and price data of the most recently added item
    private String[] addedItem = new String[2];
    //array that holds the current items the user has selected
    private String[][] currentItems = new String[0][3];
    private  int CIPointer = 0; //Points to currently available row index of currentItems array
    private float changeDue;//the change due from cash input

    //Default Constructor
    public KioskUI() {
        initialise();
    }

    //Constructor for coming back from CashInput
    public KioskUI(String[][] items, float change) {
        currentItems = items;
        CIPointer = currentItems.length;
        changeDue = change;

        CashRecieptOut();
    }

    public static void main(String[] args) {
        KioskUI kiosk = new KioskUI();
    }

    public void initialise() {
        JFrame frame = new JFrame("KioskUI");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        DefaultTableModel TModel = (DefaultTableModel) ItemsTable.getModel();

        //Setting up to read from the database
        String filename = "Stock Database.txt";
        InputStream is = FileStream(filename);
        ReadFile(is);

        //Listener for the item code submission button
        CodeSubmit.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        //gets the current text value in the code input text box
                        currentCode = CodeInput.getText();

                        //checks through the array for a matching code, if one is found the item data associated with that is added to the table model
                        //and thus the table
                        for (int count = 0; count < dataArray.length; ) {

                            if (dataArray[count][0].equals(currentCode))
                            {

                                currentItems = Array2DResize(currentItems);

                                currentItems[CIPointer][0] = dataArray[count][0];
                                addedItem[0] = currentItems[CIPointer][1] = dataArray[count][1];
                                addedItem[1] = currentItems[CIPointer][2] = dataArray[count][2];

                                TModel.addRow(addedItem);
                                CodeInput.setText("");
                                TotalCost.setText("Total: £" + TotalPrice());
                                CIPointer++;
                                count = dataArray.length;
                            }
                            //If no code is matched then a message is display to inform the user
                            else if (count == dataArray.length - 1) {
                                JOptionPane.showMessageDialog(null, "An item with that code does not exist", "Invalid Item Code", JOptionPane.INFORMATION_MESSAGE);
                                CodeInput.setText("");
                            }
                            count++;
                        }

                    }
                }
        );

        //Listener for the button to take a user to the stock database management UI
        StockDatabaseButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DatabaseLogin login = new DatabaseLogin();
                        frame.dispose();
                    }
                }
        );

        //Listener for the pay by card button
        CardButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Bank would like to check that you wish to pay £" + TotalPrice() + " via your card", "Bank Check", JOptionPane.INFORMATION_MESSAGE);
                        CodeInput.setEditable(false);
                        QuantityAdjust(is);
                        RecieptText.setText(RecieptWrite());
                    }
                }
        );
        //Listener for the pay by cash button
        CashButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        CashInput Cash = new CashInput(TotalPrice(), currentItems);
                        frame.dispose();
                    }

                }
        );
        //Listener for the Remove Item Button
        RemoveItemButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        removeItem2DArray(currentItems, ItemsTable.getSelectedRow());
                        TotalCost.setText("Total: £" + TotalPrice());
                        CIPointer--;
                        TModel.removeRow(ItemsTable.getSelectedRow());
                    }
                }
        );
    }

    //Alternative to initialise() for when returning from CashInput
    public void CashRecieptOut()
    {
        JFrame frame = new JFrame("KioskUI");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        DefaultTableModel TModel = (DefaultTableModel) ItemsTable.getModel();

        String filename = "Stock Database.txt";
        InputStream is = FileStream(filename);
        ReadFile(is);

        for (int count = 0; count < currentItems.length; )
        {
            addedItem[0] = currentItems[count][1];
            addedItem[1] = currentItems[count][2];
            TModel.addRow(addedItem);
            TotalCost.setText("Total: £" + TotalPrice());
            count++;
        }

        CodeInput.setEditable(false);
        QuantityAdjust(is);
        RecieptText.setText(RecieptWrite() + "\n" + "Total Change:" + addSpace(17) + "£"+ changeDue);
    }

    //Gets the database info as a Input Stream
    private InputStream FileStream(String fileName) {
        ClassLoader csLoader = getClass().getClassLoader();
        InputStream inStream = csLoader.getResourceAsStream(fileName);

        if (inStream == null) {
            throw new IllegalArgumentException("File not found" + fileName);
        } else {
            return inStream;
        }

    }

    //Reads in the text file and adds them to the array
    void ReadFile(InputStream is) {
        System.out.println("Building DB Array");
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            //File MyFile = new File("Stock Database.txt");
            //Scanner reader = new Scanner(MyFile);
            String line;
            int rcount = 0;


            while ((line = reader.readLine()) != null)//.hasNextLine())
            {
                //Increasing the arrays size by 1 to account for new data
                dataArray = Array2DResize(dataArray);

                //Item Code
                dataArray[rcount][0] = line.substring(0, 5);
                //Item Name
                dataArray[rcount][1] = line.substring(9, 39);
                //Item Price
                dataArray[rcount][2] = line.substring(39, 48);
                //Item Quantity
                dataArray[rcount][3] = line.substring(48, 51);
                rcount++;
            }
        } catch (IOException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
        }
    }

    //Increases a 2d array's size by 1 row
    String[][] Array2DResize(String[][] currArray) {
        int length = currArray.length;
        String[][] newArray = new String[length + 1][4];

        for (int count = 0; count < length; )
        {
            //Copying the old array's data into the new, bigger array
            newArray[count][0] = currArray[count][0];
            newArray[count][1] = currArray[count][1];
            newArray[count][2] = currArray[count][2];
            newArray[count][3] = currArray[count][3];
            count++;
        }

        return newArray;
    }

    //Calculates the current total price
    String TotalPrice() {
        float total = 0;

        for (int count = 0; count < ItemsTable.getRowCount(); ) {
            String strPrice = (String) ItemsTable.getValueAt(count, 1);
            total = total + Float.parseFloat(rounding(strPrice.substring(1, (strPrice.length()))));

            count++;
        }
        total = Float.parseFloat(rounding(String.valueOf(total)));
        return TwoDecimalPlaces(total);

    }

    //reduces numbers to two decimal places
    String rounding(String value) {
        String whole;
        String deci;
        for (int count = 0; count < value.length(); ) {
            if (value.charAt(count) == '.')
            {
                whole = value.substring(0, count);
                deci = value.substring(count + 1, value.length());
                //if (deci.length() > 2 && Integer.parseInt(String.valueOf(deci.charAt(1))) >= 5) {
                    //String tenth = String.valueOf(deci.charAt(0));
                    //String hundredth = String.valueOf(Integer.parseInt(String.valueOf(deci.charAt(1))) + 1);
                    //deci = tenth + hundredth;
                 if (deci.length() > 2)
                 {
                    deci = String.valueOf(deci.charAt(0)) + deci.charAt(1);
                 }
                count = value.length();
                value = whole + "." + deci;
            }
            count++;
        }
        return value;
    }

    //Adds a zero if the number only has 1 decimal place
    String TwoDecimalPlaces(float val) {
        String value = String.valueOf(val);
        String whole;
        String deci;
        for (int count = 0; count < value.length(); ) {
            if (value.charAt(count) == '.') {
                whole = value.substring(0, count);
                deci = value.substring(count + 1, value.length());
                if (deci.length() < 2) {
                    deci = deci + "0";
                }
                count = value.length();
                value = whole + "." + deci;
            }
            count++;
        }
        return value;

    }

    //used to custom create UI components
    private void createUIComponents() {
        //Creating a table with a model that has uneditible cells
        ItemsTable = new JTable(new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });


    }

    //Constructs the Reciept output
    String RecieptWrite()
    {
        String RecieptItems = "";
        String RecieptData = "      Super Shop     ";
        RecieptData = RecieptData + "\n";
        RecieptData = RecieptData +"Date: " +  java.time.LocalDate.now() + "\n" + "\n";
        for(int count = 0; count < ItemsTable.getRowCount();)
        {
            String name = (String)ItemsTable.getValueAt(count, 0);
            name = substringBlankRemover(name);
            RecieptItems = RecieptItems + name + addSpace(30 - name.length()) + ItemsTable.getValueAt(count, 1);
            RecieptItems = RecieptItems + "\n";
            count++;
        }
        RecieptData = RecieptData + RecieptItems;
        RecieptData = RecieptData + "Total:" + addSpace(24) + "£" + TotalPrice();

        return  RecieptData;
    }

    //Used to add spaces to strings, so formatting is kept in the database
    private String addSpace(int spaces)
    {
        String space = "";

        for(int count = 0; count < spaces;)
        {
            space = space + " ";
            count++;
        }
        return space;
    }

    //Removes spaces from a string
    private String substringBlankRemover(String check)
    {
        String out = "";
        for(int count = 0; count < check.length()-1;)
        {
            if(check.charAt(count) != ' ')
            {
                out = out + check.charAt(count);
            }
            else if (check.charAt(count) == ' ' && check.charAt(count + 1) !=' ')
            {
               out = out + check.charAt(count);
            }

            if(count == check.length() -2 && check.charAt(count + 1) != ' ')
            {
                out = out + check.charAt(count + 1);
            }
            count++;
        }
        return out;
    }

    //Adjusts the quantities in the stock database based on selected items
    private void QuantityAdjust(InputStream is)
    {
        for(int count = 0; count < currentItems.length;)
        {
            for(int dcount =0; dcount < dataArray.length;)
            {
                if(currentItems[count][0].equals(dataArray[dcount][0]))
                {
                    dataArray[dcount][3] = String.valueOf(Integer.parseInt(substringBlankRemover(dataArray[dcount][3])) - 1);
                }
                dcount++;
            }
            count++;
        }

        String data = "";
        for(int count = 0; count < dataArray.length;)
        {
            data = data + dataArray[count][0] + addSpace(4);

            String name = dataArray[count][1];
            data = data + name + addSpace(30 - name.length());

            String price = dataArray[count][2];
            data = data + price + addSpace(9 - price.length());

            String quantity = dataArray[count][3];
            data = data + quantity;

            if(quantity.length() < 3)
            {
                data = data + addSpace(3-quantity.length());
            }

            data = data + "\n";
            count++;
        }
        FileWrite(data);
        ReadFile(is);
    }

    //Writes to a file
    void FileWrite(String data)
    {
        System.out.println("Writing to Database");
        try {
            Files.write(Paths.get("resources/Stock Database.txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Removes an item from an array and shinks the array accordingly
    String[][] removeItem2DArray(String[][] data, int removeIndex)
    {
        if(removeIndex != data.length - 1)
        {
            data[removeIndex][0] = data[removeIndex + 1][0];
            data[removeIndex][1] = data[removeIndex + 1][1];
            data[removeIndex][2] = data[removeIndex + 1][2];
            data[removeIndex][3] = data[removeIndex + 1][3];

            for(int count = removeIndex + 2; count < data.length;)
            {
                data[count - 1][0] = data[count][0];
                data[count - 1][1] = data[count][1];
                data[count - 1][2] = data[count][2];
                data[count - 1][3] = data[count][3];
            }
            Array2DReduce(data);
        }
        else{
            Array2DReduce(data);
        }
        return data;

    }

    //Reduces an array's size by 1
    String[][] Array2DReduce(String[][] currArray) {
        int length = currArray.length;
        String[][] newArray = new String[length-1][4];

        for (int count = 0; count < length - 1; )
        {
            //Copying the old array's data into the new, bigger array
            newArray[count][0] = currArray[count][0];
            newArray[count][1] = currArray[count][1];
            newArray[count][2] = currArray[count][2];
            newArray[count][3] = currArray[count][3];
            count++;
        }

        return newArray;
    }
}


