package kta02.domein;

public class Klant
{

    private String voornaam;
    private String achternaam;
    private String adres;
    private String postcode;
    private String plaats;

    public String getAchternaam()
    {
        return achternaam;
    }

    public String getPlaats()
    {
        return plaats;
    }

    public void setVoornaam(String voornaam)
    {
        this.voornaam = voornaam;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public void setPlaats(String plaats)
    {
        this.plaats = plaats;
    }

    public void setAdres(String adres)
    {
        this.adres = adres;
    }

    public void setAchternaam(String achternaam)
    {
        this.achternaam = achternaam;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public String getAdres()
    {
        return adres;
    }

    public String getVoornaam()
    {
        return voornaam;
    }

}
