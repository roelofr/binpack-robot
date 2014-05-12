package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import kta02.domein.Bestelling;
import kta02.domein.PackageLocation;

public class DatabaseProcessor
{
//USBWEBSERVER

    Bestelling order;

    public DatabaseProcessor(Bestelling order)
    {
        this.order = order;
    }

    public ArrayList<PackageLocation> processArticles() throws SQLException
    {
        ArrayList<PackageLocation> foundPackages = new ArrayList<>();

        for (int artikelNr : order.getArtikelnummers())
        {
            ResultSet rs = DatabaseQueryCollector.getInstance().getLocation(artikelNr);

            while (rs.next())
            {
                int posx = rs.getInt("posX");
                int posy = rs.getInt("posY");

                foundPackages.add(new PackageLocation(posx, posy, artikelNr));

            }
        }

        return foundPackages;

    }

}
