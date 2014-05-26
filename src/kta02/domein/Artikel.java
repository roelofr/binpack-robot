package kta02.domein;

import java.awt.Point;

/**
 *
 * @author Sander
 */
public class Artikel
{

    String beschrijving;
    int artikelnr;
    Point locatie;
    int size;

    public Artikel(int artikelnr)
    {
        beschrijving = "";
        locatie = new Point(0, 0);
        this.artikelnr = artikelnr;
        size = 0;

    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getArtikelnr()
    {
        return artikelnr;
    }

    public String getBeschrijving()
    {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving)
    {
        this.beschrijving = beschrijving;
    }

    public void setLocatie(Point locatie)
    {
        this.locatie = locatie;
    }

    public Point getLocatie()
    {
        return locatie;
    }

    public String toString()
    {
        return "Artikelnummer: " + artikelnr + " is een " + beschrijving + ". Locatie: " + locatie.getX() + " X, " + locatie.getY() + " Y . Grootte: " + size;
    }

}
