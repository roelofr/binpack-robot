/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class MainGUI extends JFrame
{

    JFrame frame;
    JPanel screenContent;
    ArduinoList connectedDevices;
    CurrentStatusPanel statusPanel;

    ArrayList<ArduinoConnection> arduinoList;

    Warehouse wh;

    boolean currentHasSelectedFile = false;

    public MainGUI(Warehouse wh)
    {

        this.wh = wh;

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

        screenContent = new JPanel(new BorderLayout());
        add(screenContent, BorderLayout.CENTER);

        connectedDevices = new ArduinoList(wh);
        add(connectedDevices, BorderLayout.WEST);

        setVisible(true);

        EasterEggKeyListener listener = new EasterEggKeyListener();
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(listener);

        toggleInterface(false, true);

    }

    public void toggleInterface(boolean hasSelectedFile)
    {
        toggleInterface(hasSelectedFile, false);
    }

    private void toggleInterface(boolean hasSelectedFile, boolean forceDraw)
    {
        currentHasSelectedFile = hasSelectedFile;
        dumpCenterContent();

        if (hasSelectedFile)
        {
            fillWithCurrentStatus();
        } else
        {
            fillWithSelectButton();
        }

    }

    private void dumpCenterContent()
    {
        if (screenContent.getComponents().length > 0)
        {
            for (Component comp : screenContent.getComponents())
            {
                comp.setVisible(false);
            }

            screenContent.removeAll();
        }
    }

    private void fillWithSelectButton()
    {
        screenContent.add(new SelectOrderPanel(wh), BorderLayout.CENTER);
    }

    private void fillWithCurrentStatus()
    {
        screenContent.add(new CurrentStatusPanel(wh), BorderLayout.CENTER);

    }

    public void setArduinos(ArrayList<ArduinoConnection> arduinoList)
    {
        this.arduinoList = arduinoList;
        if (connectedDevices != null)
        {
            connectedDevices.setContent(arduinoList);
        }
    }

}
