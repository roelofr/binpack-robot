/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Bestelling;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Solid
 */
public class RobotDisplay extends JPanel
{
    Bestelling order;

    public RobotDisplay()
    {
        this.setPreferredSize(new Dimension(500, 400));
        this.setLayout(new FlowLayout());

        this.setVisible(true);

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (order == null)
        {
            order = Warehouse.getBestelling();
            if (order == null)
            {
                return;
            }
        }

        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                g.drawRect(x * 75 + 150, y * 75, 75, 75);
            }
        }
        g.drawRect(0, 150, 75, 150);
        g.setColor(Color.red);

        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                this.paintItem(x, y, 15, g, Color.lightGray);
            }
        }

        Point p;
        for (Artikel item : order.getArtikelen())
        {
            p = item.getLocatie();
            this.paintItem(p.x, p.y, 15, g, Color.magenta);
        }

    public void paintConnection(int x1, int y1, int x2, int y2, Graphics g, Color color)
    {
        g.setColor(color);
        g.drawLine(75 * x1 + 200, 300 - (75 * y1), 75 * x2 + 187, 263 - (75 * y2));
    }

    public void paintItem(int x1, int y1, int r, Graphics g, Color color)
    {
        g.setColor(color);
        g.fillOval(75 * x1 + 187 - r / 2, 263 - r / 2 - (75 * y1), r, r);
    }
}
