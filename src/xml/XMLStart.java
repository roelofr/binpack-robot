/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import domein.Bestelling;

/**
 *
 * @author Sander
 */
public class XMLStart
{

    public static void main(String[] args)
    {
        String filename = "src/xml/order.xml";//Komt xml file in
        XMLReader reader = new XMLReader(filename);

        Bestelling bestelling = reader.readFromXml();

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
