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
    private static final Dimension FILLER_MAX = new Dimension(16384, 16384);

    /**
     * Adds a Filler (<code>Box.Filler</code>) of a fixed size to the Component
     * given as first argument with the size given in the second argument at the
     * position given in argument 3
     *
     * @param parent
     * @param fillerSize
     * @param location
     */
    public static void addFiller(JComponent parent, Dimension fillerSize, String location)
    {
        Box.Filler tempFill = new Box.Filler(fillerSize, fillerSize, fillerSize);
        parent.add(tempFill, location);
    }

    /**
     * Adds a Filler (<code>Box.Filler</code>) of a fixed size to the Component
     * given as first argument with the size given in the second argument at the
     * default position
     *
     * @param parent
     * @param fillerSize
     */
    public static void addFiller(JComponent parent, Dimension fillerSize)
    {
        addFiller(parent, fillerSize, null);
    }

    /**
     * Adds a Filler (<code>Box.Filler</code>) of a flexible size to the
     * Component given as first argument with the minimum size given in the
     * second argument at the position given in argument 3. If the 4th argument
     * is true, the preferred size is set to the largest size possible.
     *
     * @param parent
     * @param fillerSize
     * @param location
     * @param startAtLargest
     */
    public static void addFlexibleFiller(JComponent parent, Dimension fillerSize, String location, boolean startAtLargest)
    {
        Box.Filler tempFill = new Box.Filler(fillerSize, startAtLargest ? FILLER_MAX : fillerSize, FILLER_MAX);
        parent.add(tempFill, location);
    }

    /**
     * Adds a Filler (<code>Box.Filler</code>) of a flexible size to the
     * Component given as first argument with the minimum size given in the
     * second argument at the position given in argument 3
     *
     * @param parent
     * @param fillerSize
     * @param location
     */
    public static void addFlexibleFiller(JComponent parent, Dimension fillerSize, String location)
    {
        addFlexibleFiller(parent, fillerSize, location, false);
    }

    /**
     * Adds a Filler (<code>Box.Filler</code>) of a flexible size to the
     * Component given as first argument with the minimum size given in the
     * second argument at the default position
     *
     * @param parent
     * @param fillerSize
     */
    public static void addFlexibleFiller(JComponent parent, Dimension fillerSize)
    {
        addFlexibleFiller(parent, fillerSize, null, false);
    }

}
