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
    private ArrayList<Artikel> artikelnummers;

    public Bestelling()
    {
        artikelnummers = new ArrayList<>();
    }

    public int getBestelNummer()
    {
        return bestelNummer;
    }

    public void voegToeArtikel(int artikelnummer)
    {
        artikelnummers.add(new Artikel(artikelnummer));
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

    public ArrayList<Artikel> getArtikelen()
    {
        return artikelnummers;
    }

    public Klant getKlant()
    {
        return klant;
    }

    public void print()
    {
        Bestelling bestelling = this;
        System.out.println("Het ordernummer is " + bestelling.getBestelNummer());
        System.out.println("De artikelnummers zijn " + bestelling.getArtikelen());
        System.out.println("De voornaam is " + bestelling.getKlant().getVoornaam());
        System.out.println("De achternaam is " + bestelling.getKlant().getAchternaam());
        System.out.println("Het adres is " + bestelling.getKlant().getAdres());
        System.out.println("De postcode is " + bestelling.getKlant().getPostcode());
        System.out.println("De plaats is " + bestelling.getKlant().getPlaats());
        System.out.println("De datum is " + bestelling.getDatum());
    }

}
