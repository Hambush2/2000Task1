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
    float totalCost;
    int sum = 0;

    public CashInput(float total)
    {
        totalCost =  total;
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
                        if(totalCost > sum)
                        {
                            JOptionPane.showMessageDialog(null, "Not enough cash inserted", "InfoBox: " + "Not Enough Cash Inserted", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else if(sum > totalCost)
                        {
                            float change = sum - totalCost;
                            JOptionPane.showMessageDialog(null, "Change Due: £" + change, "InfoBox: " + "Change Due", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                        }
                    }
                }
        );
    }
}
