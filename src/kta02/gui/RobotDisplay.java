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
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JPanel;
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
    RobotMover robotMover;

    long lastOrderUpdate;

    public RobotDisplay(Warehouse wh)
    {

        this.wh = wh;
        robotMover = wh.getRobotMover();

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

        private final RobotDisplay display;

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
        if (robotMover == null)
        {
            robotMover = wh.getRobotMover();
        }

        if (robotMover == null)
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

        Point lastPoint = new Point(0, 0);

        ArrayList<Point> fetchQueue = robotMover.getFetchQueue();

        int node = 0;

        int currentPos = robotMover.getCurrentIndex();
        int robotState = robotMover.getCurrentState();

        int pointX = robotMover.getCurrentPosX();
        int pointY = robotMover.getCurrentPosY();

        boolean isEndPos;

        if (robotState == RobotMover.STATE_DEPOSIT)
        {
            pointX = -1;
            pointY = 0;
        }
        if (pointX >= -1 && pointX <= COLUMN_COUNT)
        {
            if (pointY >= 0 && pointY <= ROW_COUNT)
            {
                g.setColor(new Color(255, 255, 200));
                g.fillRect(BOX_SIZE + pointX * BOX_SIZE + 1, pointY * BOX_SIZE + 1, BOX_SIZE - 1, BOX_SIZE - 1);
                paintBox(new Point(pointX, pointY), 15, g, pointX == -1 ? Color.green : Color.lightGray);
            }
        }

        for (Point item : fetchQueue)
        {
            isEndPos = node == (fetchQueue.size() - 1);

            if (isEndPos)
            {
                item = new Point(-1, 0);
            }

            if (node == currentPos && robotState != RobotMover.STATE_RESET)
            {
                double now = (new Date().getTime() % 1000.0);
                if (isEndPos && robotState == RobotMover.STATE_RETRIEVE)
                {
                    Point startPos = new Point(0, 0);

                    Point interim = new Point(startPos.x, lastPoint.y);

                    paintConnection(interim, lastPoint, g, Color.red);
                    drawIntermediateItem(interim, lastPoint, now, g, Color.yellow);

                    paintConnection(startPos, interim, g, Color.red);
                    drawIntermediateItem(startPos, interim, now, g, Color.yellow);

                    paintConnection(item, startPos, g, Color.red);
                    drawIntermediateItem(item, startPos, now, g, Color.yellow);

                    // Paint nodes over
                    paintItem(item, 15, g, Color.green);
                    paintItem(lastPoint, 15, g, Color.magenta);
                } else if (robotState == RobotMover.STATE_RETRIEVE && !isEndPos)
                {
                    Point interim = new Point(item.x, lastPoint.y);

                    paintConnection(interim, lastPoint, g, Color.red);
                    drawIntermediateItem(interim, lastPoint, now, g, Color.yellow);

                    paintConnection(item, interim, g, Color.red);
                    drawIntermediateItem(item, interim, now, g, Color.yellow);

                    // Paint nodes over
                    paintItem(item, 15, g, Color.red);
                    paintItem(lastPoint, 15, g, node == 0 ? Color.lightGray : Color.magenta);

                } else
                {
                    if (now % 600 > 300)
                    {
                        paintItem(item, 30, g, Color.yellow);
                        paintItem(item, 15, g, isEndPos ? Color.green : Color.red);
                    }
                }
            } else
            {
                if (!isEndPos)
                {
                    paintItem(item, 15, g, Color.magenta);
                }
            }
            node++;
            lastPoint = item;
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
