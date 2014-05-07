package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
//USBWEBSERVER

    public Connection getConnection() throws SQLException
    {
        Connection conn = null;

        String url = "jdbc:mysql://server2.tmg-clan.com:3306/kta02";//USBWEBSERVER
        String user = "kta02";//USBWEBSERVER
        String password = "uuUJaRWFhvVwvtJX";//USBWEBSERVER

        conn = DriverManager.getConnection(url, user, password);

        System.out.println("Connected!");
        return conn;
    }
}
