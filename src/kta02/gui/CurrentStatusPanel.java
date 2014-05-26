/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class CurrentStatusPanel extends JPanel
{

    JProgressBar progress;

    JLabel header;
    JButton openFileChooser;

    ArrayList<String> products;

    CurrentOrder order;
    RobotDisplay status;

    public CurrentStatusPanel(Warehouse warehouse)
    {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        PanelHeader hdr = new PanelHeader("Voortgang", new Font("Arial", Font.BOLD, 16), new Color(200, 240, 255));
        add(hdr, BorderLayout.NORTH);
        header = hdr.getLabel();

        JPanel twoColumn = new JPanel(new GridLayout(1, 2));
        add(twoColumn, BorderLayout.CENTER);

        order = new CurrentOrder(warehouse);
        twoColumn.add(order);

        products = new ArrayList<>();

        status = new RobotDisplay();
        twoColumn.add(status);
    }

}
