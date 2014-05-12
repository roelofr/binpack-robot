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
