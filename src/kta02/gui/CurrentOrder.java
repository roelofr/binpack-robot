/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class CurrentOrder extends JPanel
{

    JLabel orderHdr;
    JTextPane orderDetails;
    JButton selectFileButton;

    Warehouse wh;

    public CurrentOrder(Warehouse warehouse)
    {
        wh = warehouse;

        setLayout(new BorderLayout());
        setOpaque(false);

        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.EAST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.NORTH);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.SOUTH);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BorderLayout());
        add(inner, BorderLayout.CENTER);

        orderHdr = new JLabel("Huidige bestelling");
        orderHdr.setFont(new Font("Arial", Font.BOLD, 14));
        inner.add(orderHdr, BorderLayout.NORTH);

        orderDetails = new JTextPane();
        orderDetails.setBackground(Color.lightGray);
        inner.add(orderDetails, BorderLayout.CENTER);

        selectFileButton = new JButton("Bestand openen");
        selectFileButton.addActionListener(new selectListener());
        inner.add(selectFileButton, BorderLayout.SOUTH);
    }

    class selectListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            XMLPicker picker = new XMLPicker(wh);
        }

    }

}
