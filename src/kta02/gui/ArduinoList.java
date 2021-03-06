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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kta02.comm.ArduinoConnection;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public final class ArduinoList extends JPanel implements ActionListener
{

    ArrayList<ArduinoNode> arNodes;

    JPanel arduinoPanel;
    JButton resetButton;

    Warehouse warehouse;

    public ArduinoList(Warehouse warehouseConnection)
    {
        warehouse = warehouseConnection;

        setLayout(new BorderLayout());

        PanelHeader hdr = new PanelHeader("Verbonden apparaten", new Font("Arial", Font.BOLD, 16), new Color(200, 240, 255));

        arduinoPanel = new JPanel();
        arduinoPanel.setLayout(new BoxLayout(arduinoPanel, BoxLayout.Y_AXIS));
        arduinoPanel.setOpaque(false);

        resetButton = new JButton("Refresh");
        resetButton.setMinimumSize(new Dimension(10, 100));
        resetButton.setPreferredSize(new Dimension(10, 100));
        resetButton.addActionListener(this);
        resetButton.setEnabled(false);

        add(resetButton, BorderLayout.SOUTH);
        add(arduinoPanel, BorderLayout.CENTER);
        add(hdr, BorderLayout.NORTH);

        setMinimumSize(new Dimension(300, 100));
        setMaximumSize(new Dimension(300, 100));
        setPreferredSize(new Dimension(300, 100));

        arNodes = new ArrayList<>();
        EasyGUI.addFiller(arduinoPanel, EasyGUI.FILLER_LARGE, null);
    }

    public ArduinoList(ArrayList<ArduinoConnection> arduinoList)
    {
        super();
        setContent(arduinoList);
    }

    public void setContent(ArrayList<ArduinoConnection> arduinoList)
    {
        if (arNodes != null && arNodes.size() > 0)
        {
            for (ArduinoNode an : arNodes)
            {
                an.setVisible(false);
            }
            arNodes.clear();
        }
        arduinoPanel.removeAll();

        if (arduinoList == null || arduinoList.isEmpty())
        {

            EasyGUI.addFiller(arduinoPanel, EasyGUI.FILLER_LARGE, null);
            JLabel emptyResult = new JLabel("No devices connected");
            emptyResult.setForeground(Color.gray);
            emptyResult.setAlignmentX(CENTER_ALIGNMENT);
            emptyResult.setFont(new Font("Arial", Font.BOLD, 16));

            arduinoPanel.add(emptyResult);
        } else
        {

            for (ArduinoConnection arduino : arduinoList)
            {
                ArduinoNode arNode = new ArduinoNode(arduino, this, warehouse);
                EasyGUI.addFiller(arduinoPanel, EasyGUI.FILLER_SMALL, null);
                arduinoPanel.add(arNode);

                arNodes.add(arNode);
            }
        }
        arduinoPanel.revalidate();
        resetButton.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == resetButton)
        {
            resetButton.setEnabled(false);
            warehouse.reconnectToArduinos();
        }
    }

}
