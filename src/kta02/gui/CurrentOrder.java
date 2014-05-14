/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Roelof
 */
public class CurrentOrder extends JPanel
{

    JLabel orderHdr;
    JTextArea orderDetails;

    public CurrentOrder()
    {
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

        orderDetails = new JTextArea();
        orderDetails.setBackground(Color.gray);
        inner.add(orderDetails, BorderLayout.CENTER);
    }

}
