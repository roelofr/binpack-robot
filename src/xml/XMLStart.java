/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import kta02.domein.Bestelling;

/**
 *
 * @author Sander
 */
public class XMLStart
{

    public XMLStart(String filename)

    {
        XMLReader reader = new XMLReader(filename);

        Bestelling bestelling = reader.readFromXml();

        bestelling.print();
    }
}
