package xml;

import domein.Bestelling;
import domein.Klant;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLReader
{

    private File file;

    public XMLReader(String filename)
    {
        file = new File(filename);
    }

    public Bestelling readFromXml()
    {
        Bestelling bestelling = new Bestelling();
        Klant klant = new Klant();
        try
        {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance(); //nieuw DocumentBuilderFactory Instance
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder(); //nieuw DocumentBuilderFactory
            Document doc = docBuilder.parse(file); //bestandnaam

            doc.getDocumentElement().normalize(); //Normaliseer tekst.
            // order nummer ophalen
            NodeList ordernummerNodeList = doc.getElementsByTagName("ordernummer");
            Element ordernummerNode = (Element) ordernummerNodeList.item(0);
            NodeList ordernummerTXTList = ordernummerNode.getChildNodes();
            String ordernummer = ((Node) ordernummerTXTList.item(0)).getNodeValue().trim();
            bestelling.setBestelNummer(Integer.decode(ordernummer));

            //Klantgegevens ophalen
            //-------
            NodeList voornaamList = doc.getElementsByTagName("voornaam");
            Element voornaamElement = (Element) voornaamList.item(0);

            NodeList textvoornaamList = voornaamElement.getChildNodes();
            String voornaam = ((Node) textvoornaamList.item(0)).getNodeValue().trim();
            klant.setVoornaam(voornaam);

            //-------
            NodeList achternaamList = doc.getElementsByTagName("achternaam");
            Element achternaamElement = (Element) achternaamList.item(0);

            NodeList textachternaamList = achternaamElement.getChildNodes();
            String achternaam = ((Node) textachternaamList.item(0)).getNodeValue().trim();
            klant.setAchternaam(achternaam);

            //----
            NodeList adresList = doc.getElementsByTagName("adres");
            Element adresElement = (Element) adresList.item(0);

            NodeList textadresList = adresElement.getChildNodes();
            String adres = ((Node) textadresList.item(0)).getNodeValue().trim();
            klant.setAdres(adres);
            //----
            NodeList postcodeList = doc.getElementsByTagName("postcode");
            Element postcodeElement = (Element) postcodeList.item(0);

            NodeList textpostcodeList = postcodeElement.getChildNodes();
            String postcode = ((Node) textpostcodeList.item(0)).getNodeValue().trim();
            klant.setPostcode(postcode);
            //----
            NodeList plaatsList = doc.getElementsByTagName("plaats");
            Element plaatsElement = (Element) plaatsList.item(0);

            NodeList textplaatsList = plaatsElement.getChildNodes();
            String plaats = ((Node) textplaatsList.item(0)).getNodeValue().trim();
            klant.setPlaats(plaats);

            bestelling.setKlant(klant);

            //Datum ophalen
            NodeList datumNodeList = doc.getElementsByTagName("datum");
            Element datumNode = (Element) datumNodeList.item(0);
            NodeList datumTXTList = datumNode.getChildNodes();
            String datum = ((Node) datumTXTList.item(0)).getNodeValue().trim();
            bestelling.setDatum(datum);
            //Artikelnummers ophalen
            NodeList artikelnrNodeList = doc.getElementsByTagName("artikelnr"); //Zoek naar element met de "" tekst.
            for (int i = 0; i < artikelnrNodeList.getLength(); i++)
            {

                Element artikelnrNode = (Element) artikelnrNodeList.item(i);
                NodeList artikelnrTXTList = artikelnrNode.getChildNodes();

                String artikelnr = ((Node) artikelnrTXTList.item(0)).getNodeValue().trim();
                bestelling.voegToeArtikel(Integer.decode(artikelnr));
            }

        }
        catch (SAXParseException err)
        {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        }
        catch (SAXException e)
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return bestelling;
    }
}
