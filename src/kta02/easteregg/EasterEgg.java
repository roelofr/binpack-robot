/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.easteregg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box.Filler;
import javax.swing.JFrame;
import kta02.gui.JImagePanel;

/**
 *
 * @author Roelof
 */
public class EasterEgg extends JFrame implements Runnable
{

    SourceDataLine soundLine = null;
    Thread musicThread;

    public EasterEgg()
    {
        setLayout(new BorderLayout());
        JImagePanel pnl = new JImagePanel("../resources/egg.png");
        add(pnl, BorderLayout.CENTER);

        Dimension dim = new Dimension(8, 8);

        Filler f = new Filler(dim, dim, dim);
        add(f, BorderLayout.NORTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(512, 512 + (int) dim.getHeight());
        Point screenPos = new Point((int) (screenSize.getWidth() - frameSize.getWidth()) / 2, (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

        setBounds(new Rectangle(screenPos, frameSize));
        setTitle("Easter egg!");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        new EasterEggWindowListener(this);

    }

    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        if (b && (soundLine == null || !soundLine.isRunning()))
        {
            playMusic();
        } else if (!b && (soundLine != null && soundLine.isRunning()))
        {
            stopMusic();
        }

    }

    private void playMusic()
    {
        musicThread = new Thread(this);
        musicThread.start();
    }

    public void stopMusic()
    {
        if (musicThread != null)
        {
            musicThread.interrupt();
        }
        soundLine.stop();
    }

    @Override
    public void run()
    {
        String bip = "../resources/triumph.wav";

        int BUFFER_SIZE = 64 * 1024;  // 64 KB

        // Set up an audio input stream piped from the sound file.
        try
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(bip));
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();
            int nBytesRead = 0;
            byte[] sampledData = new byte[BUFFER_SIZE];
            while (nBytesRead != -1 && !Thread.currentThread().isInterrupted())
            {
                nBytesRead = audioInputStream.read(sampledData, 0, sampledData.length);
                if (nBytesRead >= 0)
                {
                    // Writes audio data to the mixer via this source data line.
                    soundLine.write(sampledData, 0, nBytesRead);
                }
                if (!soundLine.isRunning())
                {
                    break;
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | NullPointerException ex)
        {
            // Don't log this, it just isn't important
        } finally
        {
            if (soundLine != null)
            {
                soundLine.drain();
                soundLine.close();
            }
        }
    }
}
