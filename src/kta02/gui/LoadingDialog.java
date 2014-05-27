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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Roelof
 */
public class LoadingDialog extends JFrame
{

    LoadingDialog frame;

    @SuppressWarnings("LeakingThisInConstructor")
    public LoadingDialog(String loadingText)
    {
        setType(Type.UTILITY);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension size = new Dimension(400, 100);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
        setAlwaysOnTop(true);

        setResizable(false);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        JPanel inner = new JPanel();
        add(inner, BorderLayout.CENTER);

        inner.setLayout(new BorderLayout());
        inner.setOpaque(false);

        EasyGUI.addFiller(inner, EasyGUI.FILLER_MEDIUM, BorderLayout.NORTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_MEDIUM, BorderLayout.EAST);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_MEDIUM, BorderLayout.SOUTH);
        EasyGUI.addFiller(inner, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);

        JLabel text = new JLabel(loadingText);
        text.setOpaque(false);
        text.setFont(new Font("Arial", Font.BOLD, 20));
        text.setHorizontalAlignment(SwingConstants.CENTER);

        add(text, BorderLayout.CENTER);

        frame = this;
        setVisible(true);
    }

    public LoadingDialog()
    {
        this("Even geduld a.u.b.");
    }

}
