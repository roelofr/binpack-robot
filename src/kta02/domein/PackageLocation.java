package kta02.domein;

public class PackageLocation
{

    int x;
    int y;
    int artikelNummer;

    public PackageLocation(int x, int y, int artikelNr)
    {
        this.x = x;
        this.y = y;
        this.artikelNummer = artikelNr;
    }

    public String toString()
    {
        return "[" + artikelNummer + ": " + x + "," + y + "]";

    }

}
