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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class SelectOrderPanel extends JPanel
{

    Warehouse wh;

    public SelectOrderPanel(Warehouse wh)
    {
        this.wh = wh;

        setLayout(new BorderLayout());

        PanelHeader panelHeader = new PanelHeader("Huidige bestelling", new Font("Arial", Font.BOLD, 16), new Color(200, 240, 255));

        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel pleaseSelectLabel = new JLabel("Geen bestelling geselecteerd.");
        pleaseSelectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pleaseSelectLabel.setVerticalAlignment(SwingConstants.TOP);

        pleaseSelectLabel.setMinimumSize(new Dimension(200, 90));

        JButton selectFileButton = new JButton("Bestand openen");

        selectFileButton.setMinimumSize(new Dimension(200, 60));
        selectFileButton.addActionListener(new xmlClickListener());

        add(panelHeader, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        EasyGUI.addFiller(contentPanel, EasyGUI.FILLER_LARGE);
        EasyGUI.addFiller(contentPanel, EasyGUI.FILLER_LARGE);

        pleaseSelectLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(pleaseSelectLabel);

        EasyGUI.addFiller(contentPanel, EasyGUI.FILLER_SMALL);

        selectFileButton.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(selectFileButton);

    }

    class xmlClickListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (wh != null)
            {
                XMLPicker.createPickerAndLoader(wh, wh.getMainGUI());
            }
        }

    }

}
