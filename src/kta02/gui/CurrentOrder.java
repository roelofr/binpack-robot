/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
        setBackground(Color.white);

        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.EAST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.WEST);
        EasyGUI.addFiller(this, EasyGUI.FILLER_MEDIUM, BorderLayout.SOUTH);

        PanelHeader clientHeader = new PanelHeader("Klant", PanelHeader.FONT_SECONDARY, PanelHeader.COLOR_SECONDARY);
        add(clientHeader, BorderLayout.NORTH);

        JPanel inner = new JPanel();
        inner.setBackground(Color.white);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        add(inner, BorderLayout.CENTER);

        EasyGUI.addFiller(inner, EasyGUI.FILLER_MEDIUM);

        orderClient = new CurrentOrderClient(warehouse.getKlant());
        inner.add(orderClient);

        EasyGUI.addFlexibleFiller(inner, EasyGUI.FILLER_LARGE, null, true);

    }

    class CurrentOrderClient extends JPanel
    {

        Klant customer;

        JLabel customerName;
        JLabel customerAddress;
        JLabel customerZip;
        JLabel customerCity;

        GridBagConstraints gbc;

        public CurrentOrderClient(Klant customer)
        {
            createElements();
            setCustomer(customer);
        }

        private GridBagConstraints getBagConstraints()
        {
            if (gbc == null)
            {
                gbc = new GridBagConstraints();
            }
            return gbc;
        }

        private JLabel addLabel(String text, int x, int y, int w, int h, double hw)
        {
            JLabel tmp = new JLabel(text);

            GridBagConstraints c = getBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = x;
            c.gridy = y;
            c.gridwidth = w;
            c.gridheight = h;
            c.weightx = hw;

            add(tmp, c);

            return tmp;
        }

        private JLabel addLabel(String text, int x, int y, int w, int h)
        {
            return addLabel(text, x, y, w, h, 0.1);
        }

        private void createElements()
        {
            setBackground(Color.white);
            setLayout(new GridBagLayout());

            // From http://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
            GridBagConstraints c = getBagConstraints();

            addLabel("Naam: ", 0, 0, 1, 1);
            customerName = addLabel("", 1, 0, 1, 1, 1);

            addLabel("Adres: ", 0, 1, 1, 1);
            customerAddress = addLabel("", 1, 1, 1, 1, 1);

            addLabel("Postcode: ", 0, 2, 1, 1);
            customerZip = addLabel("", 1, 2, 1, 1, 1);

            addLabel("Plaats: ", 0, 3, 1, 1);
            customerCity = addLabel("", 1, 3, 1, 1, 1);

        }

        private void updateFields()
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

        public void setCustomer(Klant customer)
        {
            this.customer = customer;
            updateFields();
        }

    }

}
