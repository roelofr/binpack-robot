/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
        setType(Type.UTILITY);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel inner = new JPanel();
        add(inner);

        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.NORTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.EAST);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.SOUTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_LARGE, BorderLayout.WEST);

        JPanel pnl = new JPanel(new GridLayout(2, 1));

        inner.add(pnl, BorderLayout.CENTER);

        JLabel lbl = new JLabel("Noodstop");
        lbl.setFont(new Font("Arial", Font.BOLD, 30));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.BOTTOM);
        lbl.setForeground(Color.red);

        reset = new JButton("Systeem herstellen");
        reset.addActionListener(this);

        pnl.add(lbl);
        pnl.add(reset);
        setVisible(true);

        Dimension size = new Dimension(260, 280);

        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);

        setLocationRelativeTo(null);
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
