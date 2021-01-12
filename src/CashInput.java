import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashInput
{
    private JButton Pound1Button;
    private JButton Pound10Button;
    private JButton Pound2Button;
    private JButton Pound5Button;
    private JButton Pound20Button;
    private JTextField TotalCashInText;
    private JTextField TotalCostText;
    private JButton PayButton;
    private JPanel MainPanel;
    String totalCost;
    int sum = 0;
    String[][] currentItems;

    public CashInput(String total, String[][] items)
    {
        totalCost =  total;
        currentItems = items;
        initialise();
    }


    public void initialise()
    {
        JFrame frame = new JFrame("KioskUI");
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        TotalCostText.setText("£" + totalCost);

        Pound1Button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sum = sum + 1;
                        TotalCashInText.setText("£" + sum);
                    }
                }
        );

        Pound2Button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sum = sum + 2;
                        TotalCashInText.setText("£" + sum);
                    }
                }
        );

        Pound5Button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sum = sum + 5;
                        TotalCashInText.setText("£" + sum);
                    }
                }
        );

        Pound10Button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sum = sum + 10;
                        TotalCashInText.setText("£" + sum);
                    }
                }
        );

        Pound20Button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sum = sum + 20;
                        TotalCashInText.setText("£" + sum);
                    }
                }
        );

        PayButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(Float.parseFloat(totalCost) > sum)
                        {
                            JOptionPane.showMessageDialog(null, "Not enough cash inserted", "InfoBox: " + "Not Enough Cash Inserted", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else if(sum > Float.parseFloat(totalCost))
                        {
                            float changeCalc = sum - Float.parseFloat(totalCost);
                            String change = rounding(String.valueOf(changeCalc));
                            JOptionPane.showMessageDialog(null, "Change Due: £" + change, "InfoBox: " + "Change Due", JOptionPane.INFORMATION_MESSAGE);
                            KioskUI kiosk = new KioskUI(currentItems);
                            frame.dispose();
                        }
                    }
                }
        );
    }
    String rounding(String value)
    {
        String whole;
        String deci;
        for(int count = 0; count < value.length();)
        {
            if(value.charAt(count) == '.')
            {
                whole = value.substring(0, count);
                deci = value.substring(count+1, value.length());
                if(deci.length() > 2 && Integer.parseInt(String.valueOf(deci.charAt(1))) >= 5)
                {
                    String tenth = String.valueOf(deci.charAt(0));
                    String hundredth = String.valueOf(Integer.parseInt(String.valueOf(deci.charAt(1)))+1);
                    deci = tenth + hundredth;
                }
                else if (deci.length() > 2 && Integer.parseInt(String.valueOf(deci.charAt(1))) < 5)
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
}
