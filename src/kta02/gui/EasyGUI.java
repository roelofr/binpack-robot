/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComponent;

/**
 *
 * @author Roelof
 */
public abstract class EasyGUI
{

    public static final Dimension FILLER_SMALL = new Dimension(8, 8);
    public static final Dimension FILLER_MEDIUM = new Dimension(16, 16);
    public static final Dimension FILLER_LARGE = new Dimension(50, 50);

    public static void addFiller(JComponent parent, Dimension fillerSize, String location)
    {
        Box.Filler tempFill = new Box.Filler(fillerSize, fillerSize, fillerSize);
        parent.add(tempFill, location);
    }
}
