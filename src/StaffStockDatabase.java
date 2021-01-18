import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
    private JButton addItemButton;
    private JTextField itemCodeAddText;
    private JTextField itemNameAddText;
    private JTextField itemPriceAddText;
    private JTextField itemQuantityAddText;
    private JButton removeItemButton;
    private JButton commitEditsButton;

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

        String filename = "Stock Database.txt";
        InputStream is = FileStream(filename);
        ReadFile(is, TModel);

        for(int count = 0; count < TableArray.length;)
        {
            String check;
            check = substringBlankRemover(TableArray[count][3]);
            int checkValue = Integer.parseInt(check);
            if (checkValue < 10) {
                JOptionPane.showMessageDialog(null, substringBlankRemover(TableArray[count][1]) + " is low on stock, please restock", "Stock Low Warning", JOptionPane.INFORMATION_MESSAGE);
            }
            count++;
        }

        RestockButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        int rowNum = -1;
                        String restockCode = CodeInput.getText();

                        for(int count = 0; count < TableArray.length;)
                        {
                            if(TableArray[count][0].equals(restockCode))
                            {
                                rowNum = count;
                            }
                            count++;
                        }
                        if(rowNum == -1)
                        {
                            JOptionPane.showMessageDialog(null,   "An item with that code does not exist", "Invalid Item Code", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Restock order sent for item " + substringBlankRemover(TableArray[rowNum][1]) + ". Please remember to update database when stock arrives", "InfoBox: " + "Restock Sent", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
        );

        BackButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        KioskUI kiosk = new KioskUI();
                        frame.dispose();
                    }
                }
        );

        addItemButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        if(itemCodeAddText.getText() != "" && itemQuantityAddText.getText() != "" && itemPriceAddText.getText() != "" && itemNameAddText.getText() != "")
                        {
                            String check = ItemInputCheck();
                            if(check.equals("T"))
                            {
                                Items[0] = itemCodeAddText.getText();
                                Items[1] = itemNameAddText.getText();
                                Items[2] = "£" + itemPriceAddText.getText();
                                Items[3] = itemQuantityAddText.getText();
                                TModel.addRow(Items);

                                updateDB(is, TModel);

                                JOptionPane.showMessageDialog(null, "Added new item", "Updated Stock", JOptionPane.INFORMATION_MESSAGE);

                                itemCodeAddText.setText("");
                                itemNameAddText.setText("");
                                itemPriceAddText.setText("");
                                itemQuantityAddText.setText("");
                            }
                            else {
                                JOptionPane.showMessageDialog(null, check, "Error", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                    }
                }
        );

        removeItemButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        TModel.removeRow(DatabaseTable.getSelectedRow());
                        updateDB(is, TModel);
                        JOptionPane.showMessageDialog(null,  "Removed new item", "Updated Stock", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
        );

        commitEditsButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateDB(is, TModel);
                        JOptionPane.showMessageDialog(null,  "Committed Edits", "Updated Stock", JOptionPane.INFORMATION_MESSAGE);
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
    void ReadFile(InputStream is, DefaultTableModel Tmodel)
    {
        //int modelRows = Tmodel.getRowCount();
        //for(int Icount = 0; Icount < modelRows;)
        //{
            //Tmodel.removeRow(0);
            //Icount++;
        //}

        System.out.println("Building DB Array");
        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader))
        {
            //File MyFile = new File("Stock Database.txt");
            //Scanner reader = new Scanner(MyFile);
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null)
            {
                //line = reader.nextLine();
                TableArray = Array2DResize(TableArray);

                //Item Code
                TableArray[count][0] = Items[0] = line.substring(0, 5);
                //Item Name
                TableArray[count][1] = Items[1] = line.substring(9, 39);
                //Item Price
                TableArray[count][2] = Items[2] = line.substring(39, 48);
                //Item Quantity
                TableArray[count][3] = Items[3] = line.substring(48, 51);


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
            Files.write(Paths.get("resources/Stock Database.txt"), data.getBytes());
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

        for(int count = 0; count < spaces;)
        {
            space = space + " ";
            count++;
        }
        return space;
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

    private void updateDB(InputStream is, DefaultTableModel TModel)
    {
        String data = "";
        for(int count = 0; count < DatabaseTable.getRowCount();)
        {
            data = data + DatabaseTable.getValueAt(count,0) + addSpace(4);

            String name = (String)DatabaseTable.getValueAt(count, 1);
            data = data + name + addSpace(30 - name.length());

            String price = (String) DatabaseTable.getValueAt(count, 2);
            data = data + price + addSpace(9 - price.length());

            String quantity = (String) DatabaseTable.getValueAt(count,3);
            data = data + quantity;

            if(quantity.length() < 3)
            {
                data = data + addSpace(3-quantity.length());
            }
            //String data = TableArray[count][0] + addSpace(4);
            //data = data + TableArray[count][1] + addSpace(23 - TableArray[count][1].length());
            //data = data + TableArray[count][2] + addSpace(3);
            //data = data + TableArray[count][3];

            data = data + "\n";
            count++;
        }
        FileWrite(data);

        //String filename = "Stock Database.txt";
        //is = FileStream(filename);

        ReadFile(is, TModel);

    }

    private String ItemInputCheck()
    {
        //Input validation for item code
        String error = "T";
        if(itemCodeAddText.getText().length() > 5 || itemCodeAddText.getText().length() < 5)
        {
            error = "Invalid Code Length";
        }
        if(error.equals("T"))
        {
            for (int count = 0; count < DatabaseTable.getRowCount(); )
            {

                if (itemCodeAddText.getText().equals(DatabaseTable.getValueAt(count, 0)))
                {
                    error = "Item with code " + itemCodeAddText.getText() + " already exists";
                    count = DatabaseTable.getRowCount();
                }
                count++;
            }
        }
        if(error.equals("T"))
        {
            try {
                Integer.parseInt(itemCodeAddText.getText());
            } catch (Exception e) {
                error = "Item Code cannot contain letters";
            }
        }

        //Input validation for item name
        if(error.equals("T"))
        {
            if (itemNameAddText.getText().length() > 30)
            {
                error = "Item Name too long";
            }
        }

        //Input validation for item price
        if(error.equals("T"))
        {
            if (itemPriceAddText.getText().length() > 8 || itemPriceAddText.getText().length() < 4)
            {
                error = "Price length invalid";
            }
        }
        if(error.equals("T"))
        {
            try
            {
                Float.parseFloat(itemPriceAddText.getText());
            }
            catch(Exception e)
            {
                error = "Item Price must be a decimal value";
            }
        }

        if(error.equals("T"))
        {
            if(itemQuantityAddText.getText().length() > 3)
            {
                error = "Quantity length too long";
            }
        }
        if(error.equals("T"))
        try
        {
            Integer.parseInt(itemQuantityAddText.getText());
        }
        catch(Exception e)
        {
            error = "Item Quantity must be a whole number";
        }

        return error;
    }
}
