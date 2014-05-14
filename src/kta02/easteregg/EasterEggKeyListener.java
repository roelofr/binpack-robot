/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.easteregg;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.Date;

/**
 *
 * @author Roelof
 */
public class EasterEggKeyListener implements KeyEventDispatcher
{

    private static final long PRESS_BUFFER = 150;

    int at;

    int easterEggCode[];
    char easterEggChar[];

    long last;

    EasterEgg openWindow;

    public EasterEggKeyListener()
    {
        at = 0;
        last = -1;

        easterEggCode = new int[]
        {
            KeyEvent.VK_UP,
            KeyEvent.VK_UP,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            -1,
            -1
        };
        easterEggChar = new char[]
        {

            '-',
            '-',
            '-',
            '-',
            '-',
            '-',
            '-',
            '-',
            'B',
            'A'
        };

        openWindow = new EasterEgg();
    }

    private void reset()
    {
        at = 0;
    }

    private void testDone()
    {
        if (at == easterEggCode.length)
        {
            reset();
            openWindow.setVisible(true);
        }
    }

    private void isNext(int key, char keyCode)
    {
        keyCode = Character.toUpperCase(keyCode);

        if (key == easterEggCode[at] && easterEggCode[at] != -1)
        {
            at++;
            testDone();
        } else if (keyCode == easterEggChar[at] && easterEggChar[at] != '-')
        {
            at++;
            testDone();
        } else
        {
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        char keyChar = Character.toUpperCase(e.getKeyChar());

        long now = new Date().getTime();

        if (last > now)
        {
            return true;
        }
        last = now + PRESS_BUFFER;

        if (keyCode == KeyEvent.VK_ESCAPE && openWindow.isVisible())
        {
            openWindow.stopMusic();
            openWindow.setVisible(false);
            openWindow.dispose();
            openWindow = new EasterEgg();
        }

        isNext(keyCode, keyChar);
        return true;
    }

}
