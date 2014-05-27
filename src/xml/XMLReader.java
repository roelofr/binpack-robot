package xml;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import kta02.gui.EmergencyPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XMLReader
{
    private EmergencyPanel emPanel;

    private static final int FILTER_NUM = 1;
    private static final int FILTER_WORD = 2;
    private static final int FILTER_ZIP = 3;
    private static final int FILTER_DATE = 4;
    int aantalArtikelen = 0;

    private File file;

    public XMLReader(String filename)
    {
        file = new File(filename);
    }

    /**
     *
     * @param string
     * @param what
     * @return
     */
    public String makeMatchExpression(String string, int what)
    {
        String p;
        if (what == FILTER_NUM)
        {
            p = "([^0-9]+)";
        }
        else if (what == FILTER_WORD)
        {
            p = "([^\\w ]+)";
        }
        else if (what == FILTER_ZIP)
        {
            p = "([^\\w]+)";
        }
        else if (what == FILTER_DATE)
        {
            p = "([^0-9\\-]+)";
        }
        else
        {
            return string;
        }
        return string.replaceAll(p, "");
    }

    private String getDataFromTag(Document doc, String keyName, int childNode) throws BadLocationException
    {

        NodeList nodeList = doc.getElementsByTagName(keyName);
        if (nodeList.getLength() == 0)
        {
            throw new BadLocationException("Node not found", 0);
        }

        Element temporaryElement = (Element) nodeList.item(childNode);
        NodeList temporaryElementNodes = temporaryElement.getChildNodes();
        return (String) ((Node) temporaryElementNodes.item(0)).getNodeValue().trim();
    }

    private String getFilteredDataFromTag(Document doc, String keyname, int childNode, int filter) throws BadLocationException
    {
        String keyValue = getDataFromTag(doc, keyname, childNode);
        keyValue = makeMatchExpression(keyValue, filter);
        return keyValue;
    }

    private String getFilteredDataFromTag(Document doc, String keyname, int filter) throws BadLocationException
    {
        return getFilteredDataFromTag(doc, keyname, 0, filter);
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

            String ordernummer = getFilteredDataFromTag(doc, "ordernummer", FILTER_NUM);
            bestelling.setBestelNummer(Integer.decode(ordernummer));

            String voornaam = getFilteredDataFromTag(doc, "voornaam", FILTER_WORD);
            klant.setVoornaam(voornaam);

            String achternaam = getFilteredDataFromTag(doc, "achternaam", FILTER_WORD);
            klant.setAchternaam(achternaam);

            String adres = getFilteredDataFromTag(doc, "adres", FILTER_WORD);
            klant.setAdres(adres);

            String postcode = getFilteredDataFromTag(doc, "postcode", FILTER_ZIP).toUpperCase();
            klant.setPostcode(postcode);

            String plaats = getFilteredDataFromTag(doc, "plaats", FILTER_WORD);
            klant.setPlaats(plaats);

            bestelling.setKlant(klant);

            //Datum ophalen
            String datum = getFilteredDataFromTag(doc, "datum", FILTER_DATE);
            bestelling.setDatum(datum);

            //Artikelnummers ophalen
            NodeList artikelnrNodeList = doc.getElementsByTagName("artikelnr"); //Zoek naar element met de "" tekst.
            for (int i = 0; i < artikelnrNodeList.getLength(); i++)
            {
                String artikelnr = getFilteredDataFromTag(doc, "artikelnr", i, FILTER_NUM);
                bestelling.voegToeArtikel(Integer.decode(artikelnr));
            }

        }
        catch (BadLocationException b){
            JOptionPane.showMessageDialog(emPanel, "De lay-out van uw bestelling klopt niet. Gebruik deze lay-out: http://pastebin.com/zRw2QRqf", "Lay-out Error", JOptionPane.ERROR_MESSAGE);
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
        

    public int getAantalArtikelen()
    {
        return aantalArtikelen;
    }
}
