/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Roelof
 */
public class DevMotorControll extends JPanel
{

    private static final String DEFAULT_TITLE = "Arduino control";
    private static final String DEFAULT_SEP = " - ";

    JButton motor1Buttons[];
    JButton motor2Buttons[];

    JCheckBox motor1wait;
    JCheckBox motor2wait;

    JLabel title;

    public DevMotorControll()
    {
        setOpaque(true);
        setLayout(new BorderLayout());

        title = new JLabel("Arduino Control");
        add(title, BorderLayout.NORTH);

        JPanel twoColumn = new JPanel();

    }

}
