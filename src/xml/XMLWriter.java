package kta02.xml;

import java.io.File;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLWriter
{

    Klant klantObject;
    Bestelling bestellingObject;

    public XMLWriter(Bestelling bestelling)
    {
        this.klantObject = bestelling.getKlant();
        this.bestellingObject = bestelling;
    }

    public void writeXML(String fileName)
    {
        //for loop met hoeveel pakbonnen er zijn.
        try
        {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("pakbon");
            doc.appendChild(rootElement);
            Element klant = doc.createElement("klant");
            rootElement.appendChild(klant);

            Element voornaam;
            voornaam = doc.createElement("voornaam");
            voornaam.appendChild(doc.createTextNode(klantObject.getVoornaam()));
            klant.appendChild(voornaam);

            Element achternaam = doc.createElement("achternaam");
            achternaam.appendChild(doc.createTextNode(klantObject.getAchternaam()));
            klant.appendChild(achternaam);

            Element adres = doc.createElement("adres");
            adres.appendChild(doc.createTextNode(klantObject.getAdres()));
            klant.appendChild(adres);

            Element postcode = doc.createElement("postcode");
            postcode.appendChild(doc.createTextNode(klantObject.getPostcode()));
            klant.appendChild(postcode);

            Element plaats = doc.createElement("plaats");
            plaats.appendChild(doc.createTextNode(klantObject.getPlaats()));
            klant.appendChild(plaats);

            Element bestelling = doc.createElement("bestelling");
            rootElement.appendChild(bestelling);

            for (Artikel artikel : bestellingObject.getArtikelen())
            {
                //for loop met hoeveel producten er zijn
                Element product = doc.createElement("product");
                bestelling.appendChild(product);

                Element artikelnr = doc.createElement("artikelnr");
                artikelnr.appendChild(doc.createTextNode(Integer.toString(artikel.getArtikelnr())));
                product.appendChild(artikelnr);

                Element beschrijving = doc.createElement("beschrijving");
                beschrijving.appendChild(doc.createTextNode(artikel.getBeschrijving()));
                product.appendChild(beschrijving);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(System.out);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
            File finalLocation = new File(defaultDirectory.getPath() + File.separator + fileName);

            System.out.println("Writing to " + finalLocation.getPath());

            StreamResult result = new StreamResult(finalLocation);
            transformer.transform(source, result);
            System.out.println("Pakbon gemaakt!");

        }
        catch (ParserConfigurationException pce)
        {
            pce.printStackTrace();
        }
        catch (TransformerException tfe)
        {
            tfe.printStackTrace();
        }
    }

}
