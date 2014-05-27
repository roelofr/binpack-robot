package kta02.comm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseQueryCollector
{

    private static DatabaseQueryCollector instance;

    /**
     * Returns an instance of DatabaseQueryCollector, so you only need one.
     *
     * @return
     */
    public static DatabaseQueryCollector getInstance()
    {
        return instance;
    }

    /**
     * Sets the instance of a DatabaseQueryCollector to this value
     *
     * @param inst An instance of DatabaseQueryCollector
     */
    private static void setInstance(DatabaseQueryCollector inst)
    {
        instance = inst;
    }

    private Connection dbConn;

    public DatabaseQueryCollector(Connection databaseConnection)
    {
        dbConn = databaseConnection;

        DatabaseQueryCollector.setInstance(this);
    }

    public void sendKeepAlive()
    {
        try (Statement keepAliveStatement = dbConn.createStatement())
        {
            keepAliveStatement.executeQuery("SELECT NOW()");
            keepAliveStatement.close();

        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public ResultSet getLocation(int artikel) throws SQLException
    {
        ResultSet rs;
        Statement statement;

        try
        {
            statement = dbConn.createStatement();
            rs = statement.executeQuery("SELECT posX,posY "
                    + "FROM Cell "
                    + "WHERE id = ( SELECT `cell_id` FROM `Item` WHERE `artikel_id` = '" + artikel + "')");

        } catch (SQLException e)
        {
            throw e;
        }
        return rs;

    }

    public ResultSet getDescription(int artikel) throws SQLException
    {
        ResultSet rs;
        Statement statement;

        try
        {
            statement = dbConn.createStatement();
            rs = statement.executeQuery("SELECT description "
                    + "FROM Artikel "
                    + "WHERE id = '" + artikel + "'"
            );

        } catch (SQLException e)
        {
            throw e;
        }
        return rs;
    }

    public ResultSet getSize(int artikel) throws SQLException
    {
        ResultSet rs;
        Statement statement;

        try
        {
            statement = dbConn.createStatement();
            rs = statement.executeQuery("SELECT size "
                    + "FROM Artikel "
                    + "WHERE id = '" + artikel + "'"
            );

        } catch (SQLException e)
        {
            throw e;
        }
        return rs;
    }
}
