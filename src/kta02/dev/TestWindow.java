/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.dev;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import kta02.comm.ArduinoConnection;

/**
 *
 * @author Roelof
 */
public class TestWindow implements ActionListener, Runnable
{

    private final static Dimension fillerDimension = new Dimension(64, 64);

    JFrame frame;
    JButton exitButton;
    JTable table;

    ArrayList<ArduinoConnection> arduinos;

    private Thread updateThread;

    public TestWindow(ArrayList<ArduinoConnection> arduinos)
    {
        Dimension frameSize = new Dimension(400, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point screenPos = new Point((int) (screenSize.getWidth() - frameSize.getWidth()) / 2, (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

        frame = new JFrame("Arduino Communications");
        frame.setBounds(new Rectangle(screenPos, frameSize));
        frame.setLayout(new BorderLayout());

        frame.add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.NORTH);
        frame.add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.EAST);
        frame.add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.WEST);
        frame.add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        table = new JTable(arduinos.size(), 2);
        panel.add(table, BorderLayout.CENTER);

        exitButton = new JButton("Afsluiten");
        panel.add(exitButton, BorderLayout.SOUTH);

        exitButton.addActionListener(this);

        updateThread = new Thread(this);
        updateThread.run();

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e
    )
    {
        if (e.getSource() == exitButton)
        {
            for (ArduinoConnection ar : arduinos)
            {
                ar.close();
            }
            System.exit(0);
        }
    }

    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            int index = 0;
            String type;
            for (ArduinoConnection ar : arduinos)
            {
                if (ar.getType() == ArduinoConnection.TYPE_NONE)
                {
                    type = "Unknown";
                } else if (ar.getType() == ArduinoConnection.TYPE_LOADING)
                {
                    type = "Loading...";
                } else if (ar.getType() == ArduinoConnection.TYPE_MOTOR)
                {
                    type = "Motor X-Y";
                } else if (ar.getType() == ArduinoConnection.TYPE_BIN)
                {
                    type = "Motor Z/bin";
                } else
                {
                    type = "Unknown";
                }

                table.setValueAt(index, index, 0);
                table.setValueAt(type, index, 1);

            }

            try
            {
                wait(100);
            } catch (InterruptedException ex)
            {
                System.err.printf("Updater thread interrupted! %s", ex.getMessage());
            }
        }
    }

}
