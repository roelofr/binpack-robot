package kta02.warehouse;

import database.DatabaseConnection;
import gui.GUI;
import java.sql.SQLException;

/**
 *
 * @author Huib
 */
public class Warehouse
{

    public static void main(String[] args)
    {

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");

        DatabaseConnection dbCon = new DatabaseConnection();
        try
        {
            dbCon.linkToQueryCollector();
        }
        catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }
        new GUI();
    }

}
