/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.domein;

import java.util.ArrayList;

/**
 *
 * @author Sander
 */
public class Bestelling
{

    private int bestelNummer;
    private Klant klant;
    private String datum; //Datum is niet erg belangrijk en het is makkelijker als het een String is
    private ArrayList<Integer> artikelnummers;

    public Bestelling()
    {
        this.artikelnummers = new ArrayList<Integer>();
    }

    public int getBestelNummer()
    {
        return bestelNummer;
    }

    public void voegToeArtikel(int artikelnummer)
    {
        this.artikelnummers.add(artikelnummer);
    }

    public void setBestelNummer(int bestelNummer)
    {
        this.bestelNummer = bestelNummer;
    }

    public String getDatum()
    {
        return datum;
    }

    public void setKlant(Klant klant)
    {
        this.klant = klant;
    }

    public void setDatum(String datum)
    {
        this.datum = datum;
    }

    public ArrayList<Integer> getArtikelnummers()
    {
        return this.artikelnummers;
    }

    public Klant getKlant()
    {
        return klant;
    }

    public void print()
    {
        Bestelling bestelling = this;
        System.out.println("Het ordernummer is " + bestelling.getBestelNummer());
        System.out.println("De artikelnummers zijn " + bestelling.getArtikelnummers());
        System.out.println("De voornaam is " + bestelling.getKlant().getVoornaam());
        System.out.println("De achternaam is " + bestelling.getKlant().getAchternaam());
        System.out.println("Het adres is " + bestelling.getKlant().getAdres());
        System.out.println("De postcode is " + bestelling.getKlant().getPostcode());
        System.out.println("De plaats is " + bestelling.getKlant().getPlaats());
        System.out.println("De datum is " + bestelling.getDatum());
    }

}
