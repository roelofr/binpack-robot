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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kta02.comm.ArduinoConnection;
import kta02.dev.ManualArduinoControl;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class ArduinoNode extends JPanel implements Runnable, MouseListener
{

    private static final long ARDUINO_PING_DEATH = 6000;
    private static final long ARDUINO_PING_SLOW = 2000;
    private static final String ARDUINO_IMAGE = "../resources/arduino.png";

    ArduinoConnection comm;

    JLabel header;
    JImagePanel image;
    JLabel status[];

    Thread updateThread;
    ArduinoList parent;

    Warehouse wh;

    boolean isSelected;

    public ArduinoNode(ArduinoConnection comm, ArduinoList parent, Warehouse wh)
    {
        this.comm = comm;
        this.parent = parent;
        this.wh = wh;
        isSelected = false;

        setLayout(new BorderLayout(5, 0));

        EasyGUI.addFiller(this, EasyGUI.FILLER_SMALL, BorderLayout.NORTH);
        EasyGUI.addFiller(this, EasyGUI.FILLER_SMALL, BorderLayout.SOUTH);

        try
        {
            image = new JImagePanel(ARDUINO_IMAGE);

            JPanel pnl = new JPanel(new BorderLayout());
            pnl.setOpaque(false);

            EasyGUI.addFiller(pnl, EasyGUI.FILLER_SMALL, BorderLayout.WEST);
            EasyGUI.addFiller(pnl, EasyGUI.FILLER_SMALL, BorderLayout.EAST);

            pnl.add(image, BorderLayout.CENTER);
            add(pnl, BorderLayout.WEST);

            image.setMinimumSize(new Dimension(64, 64));
        } catch (NullPointerException e)
        {
            System.err.println(e.getMessage());
        }

        setBackground(new Color(150, 210, 255));

        int panelSize = 64 + (int) (EasyGUI.FILLER_SMALL.getHeight() * 2);

        setPreferredSize(new Dimension(300, panelSize));
        setMinimumSize(new Dimension(300, panelSize));
        setMaximumSize(new Dimension(300, panelSize));

        JPanel textFrame = new JPanel(new BorderLayout());
        textFrame.setOpaque(false);
        add(textFrame, BorderLayout.CENTER);

        header = new JLabel();
        header.setFont(new Font("Arial", Font.PLAIN, 24));
        header.setText("Loading...");
        textFrame.add(header, BorderLayout.NORTH);

        JPanel subtextFrame = new JPanel(new GridLayout(2, 2));
        subtextFrame.setOpaque(false);
        textFrame.add(subtextFrame, BorderLayout.SOUTH);

        status = new JLabel[4];

        for (int i = 0; i < 4; i++)
        {
            status[i] = new JLabel();
            status[i].setText("");
            subtextFrame.add(status[i]);
            status[i].setMinimumSize(new Dimension(90, 20));
        }

        header.setMinimumSize(new Dimension(90, 40));

        setVisible(true);

        updateThread = new Thread(this);
        updateThread.start();

        addMouseListener(this);
    }

    public void onBeforeDelete()
    {
        updateThread.interrupt();
    }

    public boolean isActive()
    {
        return isSelected;
    }

    public void setActive(Boolean active)
    {
        isSelected = active;
    }

    /**
     * Override the paint method, so we can draw our circles and squares
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);

        if (isSelected)
        {
            g.setColor(new Color(255, 255, 255, 100));
            g.drawRect(0, 0, (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
        }

    }

    @Override
    public void run()
    {
        while (!this.isValid());
        int speed1;
        int speed2;
        String type;
        while (!Thread.currentThread().isInterrupted() && this.isVisible())
        {

            if (comm == null)
            {
                System.err.println("COM link lost unexpectedly!");
                break;
            }

            speed1 = speed2 = -4;
            type = comm.getTypeName();

            if (comm.getType() == ArduinoConnection.TYPE_MOTOR || comm.getType() == ArduinoConnection.TYPE_BIN)
            {
                speed1 = comm.getMotor1Velocity();
                speed2 = comm.getMotor2Velocity();

                header.setText(type);

                status[0].setText("Motor 1: " + Integer.toString(speed1));
                status[1].setText("Motor 2: " + Integer.toString(speed2));
                status[3].setText("Device: " + comm.getComPort());

                long now = new Date().getTime();

                if (comm.isOnline() && comm.getLastSeen() < now - ARDUINO_PING_SLOW)
                {
                    status[2].setText("Not responding");
                    status[2].setForeground(Color.red);
                    if (comm.getLastSeen() < now - ARDUINO_PING_DEATH)
                    {
                        this.setVisible(false);

                        wh.reconnectToArduinos();
                    }
                } else
                {
                    status[2].setText("");
                    status[2].setForeground(Color.black);
                }
            } else
            {

                header.setText(comm.getComPort());

                status[0].setText(type);
                status[1].setText("");
                status[2].setText("");
                status[3].setText("");
            }
        }
        if (!this.isVisible())

        {
            this.setVisible(false);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        ManualArduinoControl dialog = new ManualArduinoControl(comm, wh);
        dialog.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        // Nah
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        // Nope
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // Nuh-uh
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // Nein mann
    }
}
