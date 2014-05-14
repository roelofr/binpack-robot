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
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kta02.comm.ArduinoConnection;
import kta02.easteregg.EasterEggKeyListener;

/**
 *
 * @author Roelof
 */
public class MainGUI extends JFrame
{

    JFrame frame;
    ArduinoList connectedDevices;
    CurrentStatusPanel statusPanel;

    public MainGUI()
    {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(
                (int) screenSize.getWidth() / 10 * 7,
                (int) screenSize.getHeight() / 10 * 7
        );
        Point screenPos = new Point((int) (screenSize.getWidth() - frameSize.getWidth()) / 2, (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

        setLayout(new BorderLayout());
        setTitle("KTA02 - Magazijnmanagement");
        setMinimumSize(frameSize);
        setBounds(new Rectangle(screenPos, frameSize));
        setExtendedState(Frame.MAXIMIZED_BOTH);

        JPanel screenHeader = new JPanel(new BorderLayout());
        EasyGUI.addFiller(screenHeader, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);
        EasyGUI.addFiller(screenHeader, EasyGUI.FILLER_SMALL, BorderLayout.NORTH);
        EasyGUI.addFiller(screenHeader, EasyGUI.FILLER_SMALL, BorderLayout.SOUTH);
        screenHeader.setBackground(new Color(10, 150, 255));
        add(screenHeader, BorderLayout.NORTH);

        JLabel header = new JLabel(getTitle());
        header.setMinimumSize(new Dimension(50, 70));
        header.setFont(new Font("Arial", Font.BOLD, 30));
        screenHeader.add(header, BorderLayout.CENTER);

        JPanel screenContent = new JPanel(new BorderLayout());
        add(screenContent, BorderLayout.CENTER);

        connectedDevices = new ArduinoList();
        add(connectedDevices, BorderLayout.WEST);

        statusPanel = new CurrentStatusPanel();
        screenContent.add(statusPanel, BorderLayout.CENTER);

        setVisible(true);

        EasterEggKeyListener listener = new EasterEggKeyListener();
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(listener);

    }

    public void setArduinos(ArrayList<ArduinoConnection> arduinoList)
    {
        connectedDevices.setContent(arduinoList);
    }

}
