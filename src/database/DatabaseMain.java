package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMain
{
//USBWEBSERVER

    public static void main(String[] args) throws SQLException
    {
        DatabaseConnection test = new DatabaseConnection();
        Connection com = test.getConnection();
        Statement statement = com.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM afdeling");

        while (rs.next())
        {
            int id = rs.getInt(1); 	         // 1e kolom
            String naam = rs.getString("Naam");  // kolom ‘Naam’
            String ww = rs.getString(3); 	   // 3e kolom

            System.out.println(id + " " + naam + " " + ww);
        }

    }

}
