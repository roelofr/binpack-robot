/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.domein;

import java.awt.Point;

/**
 * Holds a combination of Point / Time so you can check how much time is needed
 * for the robot to reach a certain position
 *
 * @author Roelof
 */
public class PointTimeLink
{

    private Point point;
    private long travelTime;
    private long arrivalTime;

    /**
     * Creates a new PointTimeLink
     *
     * @param point The point at which the package is located
     * @param travelTime The time which is required to get to the given point
     * from the previous point
     * @param arrivalTime The timestamp at which the robot will arrive at this
     * package
     */
    public PointTimeLink(Point point, long travelTime, long arrivalTime)
    {
        this.point = point;
        this.arrivalTime = arrivalTime;
        this.travelTime = travelTime;
    }

    public PointTimeLink(Point point)
    {
        this(point, 0, 0);
    }

    /**
     * Returns the time the robot needs to travel from the last item to this one
     *
     * @return
     */
    public long getTravelTime()
    {
        return travelTime;
    }

    /**
     * Returns the timestamp at which the robot arrives at the point, as a UNIX
     * timestamp since 01/01/1970
     *
     * @return
     */
    public long getArrivalTime()
    {
        return arrivalTime;
    }

    /**
     * Returns the Point that belongs to this PointTimeLink
     *
     * @return
     */
    public Point getPoint()
    {
        return point;
    }

    public void setArrivalTime(long arrivalTime)
    {
        this.arrivalTime = arrivalTime;
    }

    public void setTravelTime(long travelTime)
    {
        this.travelTime = travelTime;
    }

}
