/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Roelof
 */
public class JImagePanel extends JLabel
{

    Image image;

    /**
     * Creates a new ImagePanel using the file given as it's image
     *
     * @param image
     */
    public JImagePanel(String imagePath) throws NullPointerException
    {
        URL path = getClass().getResource(imagePath);

        if (!new File(path.getPath()).exists())
        {
            throw new NullPointerException("Image at \"" + path.getPath() + "\" does not exist!");
        }

        image = new ImageIcon(path.getPath()).getImage();

        Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }

}
