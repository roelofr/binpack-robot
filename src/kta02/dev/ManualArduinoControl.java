/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.dev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import kta02.comm.ArduinoConnection;
import kta02.gui.EasyGUI;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public class ManualArduinoControl extends JDialog implements ActionListener, Runnable
{

    private static final String MOTOR1_CMD = ArduinoConnection.ACTION_MOTOR1;
    private static final String MOTOR2_CMD = ArduinoConnection.ACTION_MOTOR2;

    private static final String MOTOR1_SPEEDTXT = "Motor 1 speed: ";
    private static final String MOTOR2_SPEEDTXT = "Motor 2 speed: ";

    private static final String MOTOR_TEXT = "Device: ";

    private static final String MOTOR_SPEEDS[] = new String[]
    {
        ArduinoConnection.PARAM_MOTOR_BW3,
        ArduinoConnection.PARAM_MOTOR_BW2,
        ArduinoConnection.PARAM_MOTOR_BW1,
        ArduinoConnection.PARAM_MOTOR_STOP,
        ArduinoConnection.PARAM_MOTOR_FW1,
        ArduinoConnection.PARAM_MOTOR_FW2,
        ArduinoConnection.PARAM_MOTOR_FW3
    };
    private static final String MOTOR_SPEED_LABELS[] = new String[]
    {
        "<<<",
        "<<",
        "<",
        "[]",
        ">",
        ">>",
        ">>>"
    };

    ArduinoConnection connection;
    Warehouse wh;

    JLabel topLabel;
    JLabel bottomLabel;
    JLabel header;

    JButton topButtons[];
    JButton bottomButtons[];

    JButton emergencyStop;
    JButton closeBtn;
    JButton doPickup;

    Thread updateThread;

    int motorSpeed1;
    int motorSpeed2;

    public ManualArduinoControl(ArduinoConnection connection, Warehouse wh)
    {
        this.connection = connection;
        this.wh = wh;

        setLayout(new GridLayout(4, 1, 0, 6));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(450, 200));
        setPreferredSize(new Dimension(600, 200));
        setLocationRelativeTo(null);

        header = new JLabel();
        header.setText(MOTOR_TEXT + "n/a");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header);

        topLabel = new JLabel(MOTOR1_SPEEDTXT);
        bottomLabel = new JLabel(MOTOR2_SPEEDTXT);

        topButtons = generateButtonsForArray(new JButton[MOTOR_SPEEDS.length]);
        bottomButtons = generateButtonsForArray(new JButton[MOTOR_SPEEDS.length]);

        add(addButtonFrame(topLabel, topButtons));
        add(addButtonFrame(bottomLabel, bottomButtons));

        JPanel closeButtonFrame = new JPanel();
        closeButtonFrame.setLayout(new BorderLayout(0, 10));
        add(closeButtonFrame);

        closeBtn = new JButton("Sluiten");
        closeBtn.addActionListener(this);
        closeButtonFrame.add(closeBtn, BorderLayout.WEST);

        doPickup = new JButton("Oppakken");
        doPickup.addActionListener(this);
        closeButtonFrame.add(doPickup, BorderLayout.EAST);

        emergencyStop = new JButton("Noodstop");
        emergencyStop.addActionListener(this);
        emergencyStop.setBackground(Color.red);
        closeButtonFrame.add(emergencyStop, BorderLayout.CENTER);

        updateThread = new Thread(this);

        setButtonState(false);

        updateThread.start();
    }

    private JButton[] generateButtonsForArray(JButton buttons[])
    {
        for (int i = 0; i < buttons.length; i++)
        {
            if (buttons[i] == null)
            {
                buttons[i] = new JButton();
            }
            buttons[i].addActionListener(this);
            buttons[i].setText(MOTOR_SPEED_LABELS[i]);
        }

        return buttons;
    }

    private JPanel addButtonFrame(JLabel label, JButton buttons[])
    {
        JPanel frame = new JPanel();
        JPanel buttonHolder = new JPanel();

        frame.setOpaque(false);
        buttonHolder.setOpaque(false);

        frame.setLayout(new BorderLayout());
        buttonHolder.setLayout(new GridLayout(1, buttons.length));

        EasyGUI.addFiller(frame, EasyGUI.FILLER_MEDIUM, BorderLayout.CENTER);
        frame.add(label, BorderLayout.NORTH);
        frame.add(buttonHolder, BorderLayout.SOUTH);

        for (JButton btn : buttons)
        {
            buttonHolder.add(btn);
        }

        return frame;
    }

    private synchronized void setButtonState(boolean enabled)
    {
        emergencyStop.setEnabled(enabled);
        doPickup.setEnabled(enabled);
        for (int i = 0; i < MOTOR_SPEED_LABELS.length; i++)
        {
            if (topButtons[i] != null)
            {
                topButtons[i].setEnabled(enabled);
            }
            if (bottomButtons[i] != null)
            {
                bottomButtons[i].setEnabled(enabled);
            }
        }
    }

    private synchronized void emergencyStop()
    {
        setButtonState(false);
        while (connection.getMotor2Velocity() != 0 || connection.getMotor1Velocity() != 0)
        {
            if (connection.getMotor2Velocity() != 0)
            {
                if (motorSpeed2 < 4 && connection.getType() == ArduinoConnection.TYPE_MOTOR)
                {
                    motorSpeed2 = 4;
                    connection.performAction(ArduinoConnection.ACTION_MOTOR2, ArduinoConnection.PARAM_MOTOR_FW1);
                } else
                {
                    connection.performAction(ArduinoConnection.ACTION_MOTOR2, ArduinoConnection.PARAM_MOTOR_STOP);
                }
            }
            if (connection.getMotor1Velocity() != 0)
            {
                connection.performAction(ArduinoConnection.ACTION_MOTOR1, ArduinoConnection.PARAM_MOTOR_STOP);
            }
            try
            {
                Thread.sleep(100);

            } catch (InterruptedException exc)
            {
                System.err.println("Interrupted!");
            }
        }

        setButtonState(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == closeBtn)
        {
            if (updateThread != null && updateThread.isAlive() && !updateThread.isInterrupted())
            {
                updateThread.interrupt();
            }
            this.setVisible(false);
            this.dispose();
            return;
        }

        if (e.getSource() == emergencyStop)
        {
            Warehouse.emergency();
            return;
        }

        if (e.getSource() == doPickup)
        {
            emergencyStop();
            wh.startPickup();
            return;
        }

        String motorIdentifier = null;
        String motorParam = null;
        int oldSpeed = 0;

        int i = 0;
        for (JButton btn : topButtons)
        {
            if (e.getSource() == btn)
            {
                motorIdentifier = MOTOR1_CMD;
                motorParam = MOTOR_SPEEDS[i];
                oldSpeed = motorSpeed1;
                motorSpeed1 = Integer.parseInt(MOTOR_SPEEDS[i]);
                break;
            }
            i++;
        }
        if (motorParam == null)
        {

            i = 0;
            for (JButton btn : bottomButtons)
            {
                if (e.getSource() == btn)
                {
                    motorIdentifier = MOTOR2_CMD;
                    motorParam = MOTOR_SPEEDS[i];
                    oldSpeed = motorSpeed2;
                    motorSpeed2 = Integer.parseInt(MOTOR_SPEEDS[i]);
                    break;
                }
                i++;
            }
        }

        if (motorParam == null)
        {
            JOptionPane.showMessageDialog(this, "Er is iets fout gegaan: Onbekende event bron", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (oldSpeed < 4 && motorParam.equals(ArduinoConnection.PARAM_MOTOR_STOP) && connection.getType() == ArduinoConnection.TYPE_MOTOR && motorIdentifier == ArduinoConnection.ACTION_MOTOR2)
        {

            connection.performAction(motorIdentifier, ArduinoConnection.PARAM_MOTOR_FW1);
            try
            {
                Thread.sleep(100);

            } catch (InterruptedException exc)
            {

            }
            connection.performAction(motorIdentifier, ArduinoConnection.PARAM_MOTOR_STOP);

        } else
        {
            connection.performAction(motorIdentifier, motorParam);
        }
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
        }

        boolean enabled = true;

        while (!Thread.currentThread().isInterrupted())
        {
            header.setText(MOTOR_TEXT + connection.getTypeName());

            if (connection.isValidArduino())
            {

                topLabel.setText(MOTOR1_SPEEDTXT + connection.getMotor1Velocity());
                bottomLabel.setText(MOTOR2_SPEEDTXT + connection.getMotor2Velocity());
                enabled = true;
            } else
            {
                topLabel.setText(MOTOR1_SPEEDTXT + connection.getTypeName());
                enabled = false;
            }

            setButtonState(enabled);

            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
            }
        }

        System.err.println("Thread stopped");
    }

}
