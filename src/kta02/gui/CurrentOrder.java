/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kta02.domein.Klant;
import kta02.warehouse.Warehouse;

/**
 * This is a panel which is shown in the center column of the application. It
 * contains the customer and order information and has buttons to open a new XML
 * file which get enabled when the current order has been completely retrieved.
 *
 * @author Roelof
 */
public class CurrentOrder extends JPanel
{

    /**
     * Client display info
     */
    CurrentOrderClient orderClient;

    /**
     * Button to select an XML file, disabled during a fetch of a queue
     */
    JButton selectFileButton;

    /**
     * Reference to the Warheouse
     */
    Warehouse wh;

    /**
     * Creates a new CurrentOrder and adds all the children to it.
     *
     * @param warehouse Reference to the warehouse
     */
    public CurrentOrder(Warehouse warehouse)
    {
        wh = warehouse;

        setLayout(new BorderLayout());
        setOpaque(false);

        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.EAST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.NORTH);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.SOUTH);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        add(inner, BorderLayout.CENTER);

        PanelHeader clientHeader = new PanelHeader("Klant", PanelHeader.FONT_SECONDARY, PanelHeader.COLOR_SECONDARY);
        inner.add(clientHeader);

        orderClient = new CurrentOrderClient(warehouse.getKlant());
        inner.add(orderClient);

    }

    class CurrentOrderClient extends JPanel
    {

        Klant customer;

        JLabel customerName;
        JLabel customerAddress;
        JLabel customerZip;
        JLabel customerCity;

        public CurrentOrderClient()
        {
            setLayout(new GridBagLayout());

            // From http://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
            GridBagConstraints c = new GridBagConstraints();

            customerName = new JLabel();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            c.gridheight = 1;
            c.weightx = 0.0;
            add(customerName, c);

            customerAddress = new JLabel();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            c.gridheight = 1;
            c.weightx = 0.0;
            add(customerAddress, c);

            customerZip = new JLabel();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.5;
            add(customerZip, c);

            customerCity = new JLabel();
            c.gridx = 1;
            c.gridy = 2;
            add(customerCity, c);

        }

        public CurrentOrderClient(Klant customer)
        {
            super();
            setCustomer(customer);
        }

        public void updateFields()
        {
            if (customer == null)
            {
                return;
            }
            if (!customer.isValid())
            {
                return;
            }

            customerName.setText(customer.getVoornaam() + " " + customer.getAchternaam());
            customerAddress.setText(customer.getAdres());
            customerZip.setText(customer.getPostcode());
            customerCity.setText(customer.getPlaats());
        }

        public void setCustomer(Klant klant)
        {
            if (klant != null)
            {
                this.customer = klant;
                updateFields();
            }
        }

    }

}
