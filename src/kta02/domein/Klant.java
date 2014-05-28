package kta02.domein;

public class Klant
{

    private String voornaam;
    private String achternaam;
    private String adres;
    private String postcode;
    private String plaats;

    public void setAchternaam(String achternaam)
    {
        this.achternaam = achternaam;
    }

    public void setAdres(String adres)
    {
        this.adres = adres;
    }

    public void setPlaats(String plaats)
    {
        this.plaats = plaats;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public void setVoornaam(String voornaam)
    {
        this.voornaam = voornaam;
    }

    public String getAchternaam()
    {
        return achternaam;
    }

    public String getAdres()
    {
        return adres;
    }

    public String getPlaats()
    {
        return plaats;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public String getVoornaam()
    {
        return voornaam;
    }

    public boolean isValid()
    {
        return voornaam != null && achternaam != null && adres != null && plaats != null && postcode != null;
    }
}
