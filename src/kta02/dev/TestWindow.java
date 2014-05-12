/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.dev;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import kta02.comm.ArduinoConnection;

/**
 *
 * @author Roelof
 */
public class TestWindow extends JFrame implements ActionListener, FocusListener, Runnable
{

    private final static Dimension fillerDimension = new Dimension(8, 8);

    JButton exitButton;
    JLabel label;
    JTable table;

    JButton actionButtons[];

    ArrayList<ArduinoConnection> arduinos;

    private Thread updateThread;

    private ArduinoConnection availableConn;

    public TestWindow()
    {

        Dimension frameSize = new Dimension(700, 400);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point screenPos = new Point((int) (screenSize.getWidth() - frameSize.getWidth()) / 2, (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);

        setTitle("Arduino Communications");
        setBounds(new Rectangle(screenPos, frameSize));
        setLayout(new BorderLayout());

        add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.NORTH);
        add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.EAST);
        add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.WEST);
        add(new Box.Filler(TestWindow.fillerDimension, TestWindow.fillerDimension, TestWindow.fillerDimension), BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        label = new JLabel("Loading...");
        label.setMinimumSize(new Dimension(16, 32));
        panel.add(label, BorderLayout.NORTH);

        table = new JTable(20, 3);
        table.setColumnSelectionAllowed(false);
        table.addFocusListener(this);
        panel.add(table, BorderLayout.CENTER);

        table.setValueAt("ID", 0, 0);
        table.setValueAt("Port", 0, 1);
        table.setValueAt("Type", 0, 2);

        table.setIntercellSpacing(new Dimension(2, 2));

        JPanel buttonContainer = new JPanel(new BorderLayout());
        panel.add(buttonContainer, BorderLayout.SOUTH);

        JPanel buttonRow = new JPanel(new GridLayout());
        buttonRow.setMinimumSize(new Dimension(16, 32));
        buttonContainer.add(buttonRow, BorderLayout.CENTER);

        JPanel exitButtonRow = new JPanel(new GridLayout());
        exitButtonRow.setMinimumSize(new Dimension(16, 32));
        buttonContainer.add(exitButtonRow, BorderLayout.SOUTH);

        actionButtons = new JButton[7];
        for (int i = 0; i < 7; i++)
        {
            JButton btn = new JButton();
            buttonRow.add(btn);
            btn.addActionListener(this);
            btn.setEnabled(false);
            actionButtons[i] = btn;
        }

        actionButtons[0].setText("<<<");
        actionButtons[1].setText("<<");
        actionButtons[2].setText("<");
        actionButtons[3].setText("Stop");
        actionButtons[4].setText(">");
        actionButtons[5].setText(">>");
        actionButtons[6].setText(">>>");

        exitButton = new JButton("Afsluiten");
        exitButtonRow.add(exitButton, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        exitButton.addActionListener(this);

        updateThread = new Thread(this);
        updateThread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e
    )
    {
        if (e.getSource() == exitButton)
        {
            if (arduinos != null && !arduinos.isEmpty())
            {
                for (ArduinoConnection ar : arduinos)
                {
                    ar.close();
                }
            }
            System.exit(0);
        }

        if (availableConn != null)
        {

            String arg[] = new String[7];
            for (int i = 0; i < 7; i++)
            {
                arg[i] = Integer.toString(i + 1);
            }
            int i = 0;
            for (JButton btn : actionButtons)
            {
                if (e.getSource() == btn)
                {
                    availableConn.performAction(ArduinoConnection.ACTION_MOTOR1, arg[i]);
                }
                i++;
            }
        }
    }

    public void setArduinoList(ArrayList<ArduinoConnection> arduinoList)
    {
        System.out.println("Arduino list set");
        this.arduinos = arduinoList;
    }

    /**
     * Runs the thread
     */
    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            if (arduinos != null && !arduinos.isEmpty())
            {
                label.setText("Connected to " + arduinos.size() + " arduino(s).");

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
                    } else if (ar.getType() == ArduinoConnection.TYPE_OFFLINE)
                    {
                        type = "Waiting...";
                    } else
                    {
                        type = "Unknown";
                    }

                    if (ar.getType() == ArduinoConnection.TYPE_MOTOR || ar.getType() == ArduinoConnection.TYPE_BIN)
                    {
                        availableConn = ar;
                    } else if (availableConn == ar)
                    {
                        availableConn = null;
                    }

                    table.setValueAt(index, index + 1, 0);
                    table.setValueAt(ar.getName(), index + 1, 1);
                    table.setValueAt(type, index + 1, 2);

                }
            } else
            {
                int dotcount = (int) ((new Date().getTime() / 1000) % 3);
                String dots = "";
                while (dotcount > 0)
                {
                    dotcount--;
                    dots.concat(".");
                }
                label.setText("Loading" + dots);
            }

            Boolean bool = availableConn != null;
            for (JButton btn : actionButtons)
            {
                btn.setEnabled(bool);
            }

            try
            {
                Thread.sleep((long) 100);
            } catch (InterruptedException ex)
            {
                System.err.printf("Updater thread interrupted! %s", ex.getMessage());
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
