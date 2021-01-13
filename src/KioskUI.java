import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
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

    private String currentCode;
    private String[][] dataArray = new String[0][4];
    private String[] ColumnNames = {"Item Name", "Price"};
    private String[] addedItem = new String[2];
    private String[][] currentItems = new String[0][2];
    private float changeDue;

    public KioskUI() {
        initialise();
    }

    //Constructor for coming back from CashInput
    public KioskUI(String[][] items, float change) {
        currentItems = items;
        changeDue = change;

        RecieptOut();
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

        if (currentItems.length != 0) {
            for (int count = 0; count < currentItems.length; ) {
                addedItem[0] = currentItems[count][0];
                addedItem[1] = currentItems[count][1];
                TModel.addRow(addedItem);
                TotalCost.setText("Total: £" + TotalPrice());
                count++;
            }
        }

        String filename = "Stock Database.txt";
        InputStream is = FileStream(filename);
        ReadFile(is);

        CodeSubmit.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //System.out.println("Submitting Code");
                        currentCode = CodeInput.getText();
                        for (int count = 0; count < dataArray.length; ) {

                            if (dataArray[count][0].equals(currentCode)) {

                                addedItem[0] = dataArray[count][1];
                                addedItem[1] = dataArray[count][2];

                                TModel.addRow(addedItem);
                                CodeInput.setText("");
                                TotalCost.setText("Total: £" + TotalPrice());
                                count = dataArray.length;
                            } else if (count == dataArray.length - 1) {
                                JOptionPane.showMessageDialog(null, "An item with that code does not exist", "Invalid Item Code", JOptionPane.INFORMATION_MESSAGE);
                                CodeInput.setText("");
                            }
                            count++;
                        }
                        //tblItems = new JTable(currentItems.length - 1, 2);

                    }
                }
        );

        StockDatabaseButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DatabaseLogin login = new DatabaseLogin();
                        //System.out.println("Loading Login");
                        frame.dispose();


                    }
                }
        );

        CardButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Bank would like to check that you wish to pay £" + TotalPrice() + " via your card", "Bank Check", JOptionPane.INFORMATION_MESSAGE);
                        CodeInput.setEditable(false);
                        RecieptText.setText(RecieptWrite());
                    }
                }
        );

        CashButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int count = 0; count < ItemsTable.getRowCount(); ) {
                            currentItems = Array2DResize(currentItems);

                            currentItems[count][0] = (String) ItemsTable.getValueAt(count, 0);
                            currentItems[count][1] = (String) ItemsTable.getValueAt(count, 1);
                            count++;
                        }
                        CashInput Cash = new CashInput(TotalPrice(), currentItems);
                        frame.dispose();
                    }

                }
        );

        RemoveItemButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        TModel.removeRow(ItemsTable.getSelectedRow());
                    }
                }
        );
    }

    public void RecieptOut()
    {
        JFrame frame = new JFrame("KioskUI");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        DefaultTableModel TModel = (DefaultTableModel) ItemsTable.getModel();

        if (currentItems.length != 0) {
            for (int count = 0; count < currentItems.length; ) {
                addedItem[0] = currentItems[count][0];
                addedItem[1] = currentItems[count][1];
                TModel.addRow(addedItem);
                TotalCost.setText("Total: £" + TotalPrice());
                count++;
            }
        }

        CodeInput.setEditable(false);

        RecieptText.setText(RecieptWrite() + "\n" + "Total Change:" + addSpace(17) + "£"+ changeDue);
    }

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
                //line = reader.nextLine();
                dataArray = Array2DResize(dataArray);

                //Item Code
                dataArray[rcount][0] = line.substring(0, 5);
                //Item Name
                dataArray[rcount][1] = line.substring(9, 29);
                //Item Price
                dataArray[rcount][2] = line.substring(30, 35);
                //Item Quantity
                dataArray[rcount][3] = line.substring(39, 42);
                rcount++;
            }
        } catch (IOException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
        }
    }

    String[][] Array2DResize(String[][] currArray) {
        int length = currArray.length;
        String[][] newArray = new String[length + 1][4];

        for (int count = 0; count < length; ) {
            newArray[count][0] = currArray[count][0];
            newArray[count][1] = currArray[count][1];
            newArray[count][2] = currArray[count][2];
            newArray[count][3] = currArray[count][3];
            count++;
        }

        return newArray;
    }

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

    String rounding(String value) {
        String whole;
        String deci;
        for (int count = 0; count < value.length(); ) {
            if (value.charAt(count) == '.') {
                whole = value.substring(0, count);
                deci = value.substring(count + 1, value.length());
                if (deci.length() > 2 && Integer.parseInt(String.valueOf(deci.charAt(1))) >= 5) {
                    String tenth = String.valueOf(deci.charAt(0));
                    String hundredth = String.valueOf(Integer.parseInt(String.valueOf(deci.charAt(1))) + 1);
                    deci = tenth + hundredth;
                } else if (deci.length() > 2 && Integer.parseInt(String.valueOf(deci.charAt(1))) < 5) {
                    deci = String.valueOf(deci.charAt(0)) + deci.charAt(1);
                }
                count = value.length();
                value = whole + "." + deci;
            }
            count++;
        }
        return value;
    }

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

    private void createUIComponents() {
        ItemsTable = new JTable(new DefaultTableModel(ColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });


    }

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
}


