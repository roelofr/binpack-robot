package kta02.warehouse;

import database.DatabaseConnection;
import database.DatabaseProcessor;
import gui.GUI;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import kta02.xml.XMLWriter;
import kta02.tsp.Algoritm;
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

    public static void setXMLFile(File file)
    {
        System.out.println("start");
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
        
        
        //TEST FOR ALGORITHM
        //REMOVE WHEN DONE
        
        ArrayList<Integer> orderSorting = Algoritm.tourImprovement(bestelling.getArtikelen(), 0, 0);
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
