package database;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;

public class DatabaseProcessor
{
//USBWEBSERVER

    Bestelling order;

    public DatabaseProcessor(Bestelling order)
    {
        this.order = order;
    }

    public ArrayList<Artikel> processArticles() throws SQLException
    {
        ArrayList<Artikel> artikelen = order.getArtikelen();
        int artikelNr;
        for (Artikel artikel : artikelen)
        {
            artikelNr = artikel.getArtikelnr();
            // Step 1, get the cell position
            ResultSet rs = DatabaseQueryCollector.getInstance().getLocation(artikelNr);

            while (rs.next())
            {
                int posx = rs.getInt("posX");
                int posy = rs.getInt("posY");

                artikel.setLocatie(new Point(posx, posy));

            }

            // Step 1, get the article name
            rs = DatabaseQueryCollector.getInstance().getDescription(artikelNr);

            while (rs.next())
            {
                String desc = rs.getString(1);

                artikel.setBeschrijving(desc);

            }
            // Step 3, get the size
            rs = DatabaseQueryCollector.getInstance().getSize(artikelNr);

            while (rs.next())
            {
                int size = rs.getInt("Size");

                artikel.setSize(size);
            }
        }

        return artikelen;

    }

}
