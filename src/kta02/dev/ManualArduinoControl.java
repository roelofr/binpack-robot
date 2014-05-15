/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.dev;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import kta02.comm.ArduinoConnection;
import kta02.gui.EasyGUI;

/**
 *
 * @author Roelof
 */
public class ManualArduinoControl extends JDialog implements ActionListener, Runnable
{

    private static final String MOTOR1_CMD = ArduinoConnection.ACTION_MOTOR1;
    private static final String MOTOR2_CMD = ArduinoConnection.ACTION_MOTOR2;

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

    JLabel topLabel;
    JLabel bottomLabel;

    JButton topButtons[];
    JButton bottomButtons[];

    JButton closeBtn;

    Thread updateThread;

    public ManualArduinoControl(ArduinoConnection connection)
    {
        this.connection = connection;

        setLayout(new GridLayout(4, 1, 0, 6));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(450, 200));
        setPreferredSize(new Dimension(600, 200));
        setLocationRelativeTo(null);

        topLabel = new JLabel("Motor 1 speed: ...");
        bottomLabel = new JLabel("Motor 2 speed: ...");

        topButtons = generateButtonsForArray(new JButton[MOTOR_SPEEDS.length]);
        bottomButtons = generateButtonsForArray(new JButton[MOTOR_SPEEDS.length]);

        add(addButtonFrame(topLabel, topButtons));
        add(addButtonFrame(bottomLabel, bottomButtons));

        JPanel closeButtonFrame = new JPanel();
        closeButtonFrame.setLayout(new SpringLayout());
        add(closeButtonFrame);

        closeBtn = new JButton("Sluiten");
        closeBtn.addActionListener(this);
        closeButtonFrame.add(closeBtn);

        updateThread = new Thread(this);
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
        int i = 0;
        for (JButton btn : topButtons)
        {
            if (e.getSource() == btn)
            {
                connection.performAction(MOTOR1_CMD, MOTOR_SPEEDS[i]);
                return;
            }
            i++;
        }

        i = 0;
        for (JButton btn : bottomButtons)
        {
            if (e.getSource() == btn)
            {
                connection.performAction(MOTOR2_CMD, MOTOR_SPEEDS[i]);
                return;
            }
            i++;
        }

        JOptionPane.showMessageDialog(this, "Er is iets fout gegaan: Onbekende event bron", "Error!", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void run()
    {
        while (!this.isVisible());

        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
        }

        boolean enabled = true;
        boolean oldEnabled = enabled;

        while (!Thread.currentThread().isInterrupted())
        {
            if (this.connection.isValidArduino())
            {
                if (topLabel != null)
                {
                    topLabel.setText("Motor 1: ".concat(Integer.toString(this.connection.getMotor1Velocity())));
                }

                if (bottomLabel != null)
                {
                    bottomLabel.setText("Motor 2: ".concat(Integer.toString(this.connection.getMotor2Velocity())));
                }
                enabled = true;
            } else
            {
                enabled = false;
            }

            if (enabled != oldEnabled)
            {
                oldEnabled = enabled;

                for (int i = 0; i < bottomButtons.length; i++)
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

            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
            }
        }
    }

}
