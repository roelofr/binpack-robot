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

    public static final Color COLOR_PRIMARY = new Color(230, 240, 255);
    public static final Color COLOR_SECONDARY = new Color(240, 247, 255);
    public static final Color COLOR_TERTIARY = new Color(245, 250, 255);

    public static final Font FONT_PRIMARY = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_SECONDARY = new Font("Arial", Font.BOLD, 15);
    public static final Font FONT_TERTIARY = new Font("Arial", Font.ITALIC, 14);

    private static final Font DEF_FONT = FONT_PRIMARY;
    private static final Color DEF_COLOR = COLOR_PRIMARY;

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
