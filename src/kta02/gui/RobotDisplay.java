/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Date;
import javax.swing.JPanel;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Bestelling;
import kta02.warehouse.RobotMover;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Solid
 */
public class RobotDisplay extends JPanel
{
    private final int BOX_SIZE = 75;
    private final int COLUMN_COUNT = 5;
    private final int ROW_COUNT = 4;

    private final long UPDATE_DELAY = 2000;

    Warehouse wh;
    Bestelling order;
    RobotMover robotMover;

    long lastOrderUpdate;

    public RobotDisplay(Warehouse wh)
    {

        this.wh = wh;
        robotMover = wh.getRobotMover();
        order = wh.getBestelling();

        int drawingHeight = (ROW_COUNT + 2) * BOX_SIZE + 20;
        int drawingWidth = (COLUMN_COUNT + 1) * BOX_SIZE + 20;

        this.setPreferredSize(new Dimension(drawingWidth, drawingHeight));
        this.setMinimumSize(new Dimension(drawingWidth, drawingHeight));
        this.setMaximumSize(new Dimension(1920, drawingHeight));
        this.setLayout(new FlowLayout());

        this.setVisible(true);

        RobotDisplayUpdater rdu = new RobotDisplayUpdater(this);
        new Thread(rdu).start();

    }

    class RobotDisplayUpdater implements Runnable
    {

        private RobotDisplay display;

        public RobotDisplayUpdater(RobotDisplay disp)
        {
            display = disp;
        }

        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (display != null)
                {
                    display.repaint();
                }
                try
                {
                    Thread.sleep(1000 / 45);
                } catch (InterruptedException ex)
                {
                    // Nu-uh
                }

            }
        }

    }

    private synchronized boolean getObjects()
    {
        if (order == null || new Date().getTime() < lastOrderUpdate + UPDATE_DELAY)
        {
            lastOrderUpdate = new Date().getTime();
            order = wh.getBestelling();
        }
        if (robotMover == null)
        {
            robotMover = wh.getRobotMover();
        }

        if (order == null || robotMover == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.translate(10, 10);

        paintBox(new Point(-1, 0), 15, g, Color.green);

        for (int x = 0; x < COLUMN_COUNT; x++)
        {
            for (int y = 0; y < ROW_COUNT; y++)
            {
                paintBox(new Point(x, y), 15, g, Color.lightGray);
            }
        }

        if (!getObjects())
        {
            return;
        }

        Point point;
        Point lastPoint = new Point(-1, 0);

        int arrayLength = order.getArtikelen().size();
        int robotState = RobotMover.STATE_RESET;
        int node = arrayLength;
        int currentPos = 0;
        if (robotMover != null)
            {
            currentPos = robotMover.getQueueLength();
            robotState = robotMover.getCurrentState();
            }
        for (Artikel item : order.getArtikelen())
        {
            point = item.getLocatie();
            if (node != currentPos && !(node == arrayLength && currentPos == 0) || robotState == RobotMover.STATE_RESET || robotState == RobotMover.STATE_IDLE)
            {
                paintItem(point, 15, g, Color.magenta);
            } else
            {
                paintItem(point, 15, g, Color.red);
                double now = (new Date().getTime() % 1000.0);
                if (robotState == RobotMover.STATE_RETRIEVE)
                {
                    paintConnection(point, lastPoint, g, Color.red);
                    drawIntermediateItem(point, lastPoint, now, g, Color.yellow);
                } else
                {
                    if (now % 600 > 300)
                    {
                        paintItem(point, 20, g, Color.red);
        }
                }
                node++;
                lastPoint = point;
            }
        }

        if (robotState == RobotMover.STATE_RESET)
        {
            String resetText = "Aan het resetten";
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.blue);
            FontMetrics f = g.getFontMetrics();
            int size = f.stringWidth(resetText);

            int now = (int) (new Date().getTime() % 1000);

            if (now > 750)
            {
                resetText += "...";
            } else if (now > 500)
            {
                resetText += "..";
            } else if (now > 250)
            {
                resetText += ".";
            }

            g.drawString(resetText, BOX_SIZE + COLUMN_COUNT / 2 * BOX_SIZE - (size / 2), (ROW_COUNT) * BOX_SIZE + BOX_SIZE / 2);
        }
    }

    public void paintBox(Point point, int r, Graphics g, Color color)
        {
        int x = point.x;
        int y = point.y;

        g.setColor(Color.black);
        g.drawRect(x * BOX_SIZE + BOX_SIZE, y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
        paintItem(point, r, g, color);
        }

    public void paintConnection(Point start, Point end, Graphics g, Color color)
    {
        int x1, x2, y1, y2;
        x1 = start.x;
        y1 = start.y;
        x2 = end.x;
        y2 = end.y;

        x1 = (x1 * BOX_SIZE) + (BOX_SIZE) + (BOX_SIZE / 2);
        y1 = (y1 * BOX_SIZE) + (BOX_SIZE / 2);

        x2 = (x2 * BOX_SIZE) + (BOX_SIZE) + (BOX_SIZE / 2);
        y2 = (y2 * BOX_SIZE) + (BOX_SIZE / 2);

        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }

    public void drawIntermediateItem(Point start, Point end, double mul, Graphics g, Color color)
    {
        double boxSize = (double) BOX_SIZE;

        int r = 11;

        double x = start.x;
        double y = start.y;

        double xDiff = end.x - start.x;
        double yDiff = end.y - start.y;

        mul = 1 - Math.max(Math.min(mul, 1000), 0) / 1000;

        Point goal = new Point(
                (int) ((x + xDiff * mul) * boxSize + boxSize + boxSize / 2) - r / 2,
                (int) ((y + yDiff * mul) * boxSize + boxSize / 2) - r / 2
        );

        g.setColor(color);
        g.fillOval(goal.x, goal.y, r, r);
    }

    public void paintItem(Point p, int r, Graphics g, Color color)
    {
        int x = p.x;
        int y = p.y;

        x += 1;
        x *= BOX_SIZE;
        x -= BOX_SIZE / 2;
        x += BOX_SIZE - r / 2;

        y += 1;
        y *= BOX_SIZE;
        y -= BOX_SIZE / 2;
        y -= r / 2;

        g.setColor(color);
        g.fillOval(x, y, r, r);
    }
}
