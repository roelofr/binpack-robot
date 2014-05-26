/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class EmergencyPanel extends JFrame implements ActionListener
{

    JButton reset;
    Warehouse warehouse;

    public EmergencyPanel(Warehouse wh)
    {
        warehouse = wh;
        setTitle("Emergency stop");
        setAlwaysOnTop(true);
        setAutoRequestFocus(true);
        setBounds(new Rectangle(400, 300));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel inner = new JPanel();
        add(inner);

        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.NORTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.EAST);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.SOUTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.WEST);

        JPanel pnl = new JPanel(new BorderLayout());

        inner.add(pnl, BorderLayout.CENTER);

        JLabel lbl = new JLabel("Emergency stop!");
        lbl.setFont(new Font("Arial", Font.BOLD, 30));
        lbl.setForeground(Color.red);

        pnl.add(lbl, BorderLayout.CENTER);

        reset = new JButton("Restore operation");
        reset.addActionListener(this);
        pnl.add(reset, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == reset)
        {
            reset.setEnabled(false);

            warehouse.restoreSystems();

            setVisible(false);
            dispose();
        }
    }

}
