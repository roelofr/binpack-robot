package kta02.warehouse;

import database.DatabaseConnection;
import database.DatabaseProcessor;
import gui.GUI;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.comm.ArduinoConnection;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.gui.MainGUI;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import kta02.xml.XMLWriter;
import xml.XMLReader;

/**
 *
 * @author Huib
 */
public class Warehouse
{
	
    static XMLReader reader;
    static Bestelling bestelling;

    static DatabaseProcessor dbProcessor;
    
    public static final boolean DEBUG = true;

    private static MainGUI UI;

    private static ArrayList<ArduinoConnection> arduinos;

    static ArduinoConnection conn;

    public static void main(String[] args)
    {
        setLookAndFeel();

        UI = new MainGUI();

        arduinos = new ArrayList<>();

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");
        DatabaseConnection dbCon = new DatabaseConnection();
        try
        {
            dbCon.linkToQueryCollector();
        } catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }

        // Connect to the Arduino's
        connectToArduinos();
    }

    public synchronized static void disconnectArduinos()
    {
        System.out.println("Disconnecting from arduino's");
        for (ArduinoConnection conn : arduinos)
        {
            conn.close();
        }
        arduinos.clear();

    }

    public synchronized static void connectToArduinos()
    {
        System.out.println("Connecting to arduino's");
        try
        {
            arduinos = SerialCommunicator.initialize();
        } catch (InsufficientDevicesException e)
        {
            int errorCode = e.getCode();
            if (errorCode == InsufficientDevicesException.E_NO_DEVICES)
            {
                System.err.println("No Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_HIGH)
            {
                System.err.println("Please disconnect some Arduino's, there are too many Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_LOW)
            {
                System.err.println("Please connect some Arduino's, there are insufficient Arduino's connected!");
            }
        }

        UI.setArduinos(arduinos);
    }

    public synchronized static void reconnectToArduinos()
    {
        Warehouse.disconnectArduinos();
        Warehouse.connectToArduinos();
    }

    private static void setLookAndFeel()
    {

        try
        {
            // Make it look better
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            // Do nothing
        }
    }

    public static void _devSetSelectedArduno(ArduinoConnection conn)
    {
        Warehouse.conn = conn;
    }

    public static ArduinoConnection _devGetSelectedArduno()
    {
        return conn;
    }

    public static void setXMLFile(File file)
    {
        reader = new XMLReader(file.getPath());

        bestelling = reader.readFromXml();

        dbProcessor = new DatabaseProcessor(bestelling);

        try
        {
            dbProcessor.processArticles();
            System.out.println("__________________________________________________________________");
            for (Artikel artikel : bestelling.getArtikelen())
            {
                System.out.println(artikel);
            }
            System.out.println("__________________________________________________________________");
        }
        catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }

        String bestandsNaam = "";
        String volledigeKlantNaam = bestelling.getKlant().getVoornaam() + " " + bestelling.getKlant().getAchternaam();
        bestandsNaam += bestelling.getBestelNummer() + ". " + volledigeKlantNaam;
        bestandsNaam += ".xml";

        new XMLWriter(bestelling).writeXML(bestandsNaam);

    }

    public static DatabaseProcessor getDbProcessor()
    {
        return dbProcessor;
    }

    public static Bestelling getBestelling()
    {
        return bestelling;
    }

    public static Klant getKlant()
    {
        return bestelling.getKlant();
    }

}
