package fi.om.initiative.conf.saml;

public class SamlUser {

    private final String ssn;

    private final String address;

    private String firstNames;

    private String lastName;

    private String municipalityNameFi;

    private String municipalityNameSv;

    private boolean finnishCitizen;

    public SamlUser(String ssn, String address, String firstNames, String lastName, String municipalityNameFi, String municipalityNameSv, boolean finnishCitizen) {
        this.ssn = ssn;
        this.address = address;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.municipalityNameFi = municipalityNameFi;
        this.municipalityNameSv = municipalityNameSv;
        this.finnishCitizen = finnishCitizen;
    }

    public String getSsn() {
        return ssn;
    }

    public String getAddress() {
        return address;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMunicipalityNameFi() {
        return municipalityNameFi;
    }

    public String getMunicipalityNameSv() {
        return municipalityNameSv;
    }

    public boolean isFinnishCitizen() {
        return finnishCitizen;
    }
}
