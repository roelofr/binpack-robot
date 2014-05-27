package kta02.comm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{

    Connection myConn;

    public Connection getConnection() throws SQLException
    {
        if (myConn != null)
        {
            return myConn;
        }

        Connection conn;

        String url = "jdbc:mysql://server2.tmg-clan.com:3306/kta02";
        String user = "kta02";
        String password = "uuUJaRWFhvVwvtJX";
        conn = DriverManager.getConnection(url, user, password);

        System.out.println("Connected!");

        myConn = conn;

        return conn;
    }

    /**
     * Links the connection to the QueryCollector for ease of use
     */
    public void linkToQueryCollector() throws SQLException
    {
        new DatabaseQueryCollector(getConnection());
    }
}
