/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.easteregg;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Roelof
 */
public class EasterEggWindowListener implements WindowListener
{

    EasterEgg window;

    public EasterEggWindowListener(EasterEgg window)
    {
        this.window = window;
        this.window.addWindowListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e)
    {
        //
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        window.stopMusic();

    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        //
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
        //
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {

        //
    }

    @Override
    public void windowActivated(WindowEvent e)
    {
        //
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
        //
    }
}
