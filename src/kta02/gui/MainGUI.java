/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.Box.Filler;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Roelof
 */
public class MainGUI extends JFrame
{

    public static void main(String[] args)
    {
        JFrame gui = new MainGUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static final Dimension smallFiller = new Dimension(8, 8);
    private static final Dimension mediumFiller = new Dimension(16, 16);
    private static final Dimension largeFiller = new Dimension(50, 50);

    private void addFiller(JComponent parent, Dimension fillerSize, String location)
    {
        Filler tempFill = new Filler(fillerSize, fillerSize, fillerSize);
        parent.add(tempFill, location);
    }

    public MainGUI()
    {

        Dimension frameSize = new Dimension(700, 400);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point screenPos = new Point((int) (screenSize.getWidth() - frameSize.getWidth()) / 2, (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

        setLayout(new BorderLayout());
        setTitle("KTA02 - Magazijnmanagement");
        setBounds(new Rectangle(screenPos, frameSize));

        JPanel screenHeader = new JPanel(new BorderLayout());
        addFiller(screenHeader, mediumFiller, BorderLayout.EAST);
        addFiller(screenHeader, smallFiller, BorderLayout.NORTH);
        addFiller(screenHeader, smallFiller, BorderLayout.SOUTH);

        JLabel header = new JLabel("KTA02");
        header.setMinimumSize(new Dimension(50, 70));
        header.setFont(new Font("Arial", Font.BOLD, 12));
        screenHeader.add(header, BorderLayout.CENTER);

        setVisible(true);

    }

}
