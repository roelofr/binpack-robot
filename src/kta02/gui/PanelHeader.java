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

/**
 *
 * @author Roelof
 */
public class PanelHeader extends JPanel
{

    private static final Font DEF_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Color DEF_COLOR = new Color(230, 240, 255);

    JLabel header;

    public PanelHeader(String content, Font font, Color colour)
    {
        setLayout(new BorderLayout());
        setBackground(colour);

        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_SMALL, BorderLayout.NORTH);
        EasyGUI.addFiller(this, EasyGUI.FILLER_SMALL, BorderLayout.SOUTH);

        header = new JLabel(content);
        header.setFont(font);
        add(header, BorderLayout.CENTER);
    }

    public PanelHeader(String content)
    {
        this(content, DEF_FONT, DEF_COLOR);
    }

    public PanelHeader(String content, Font font)
    {
        this(content, font, DEF_COLOR);
    }

    public PanelHeader(String content, Color colour)
    {
        this(content, DEF_FONT, colour);
    }

    public JLabel getLabel()
    {
        return header;
    }

}
