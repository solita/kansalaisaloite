package fi.om.initiative.conf.saml;

public class SamlUser {

    private final String ssn;

    private final String address;

    private String firstNames;

    private String lastName;

    private String municipalityCode;

    private String municipalityNameFi;

    private String municipalityNameSv;

    public SamlUser(String ssn, String address, String firstNames, String lastName, String municipalityCode, String municipalityNameFi, String municipalityNameSv) {
        this.ssn = ssn;
        this.address = address;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.municipalityCode = municipalityCode;
        this.municipalityNameFi = municipalityNameFi;
        this.municipalityNameSv = municipalityNameSv;
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

    public String getMunicipalityCode() {
        return municipalityCode;
    }

    public String getMunicipalityNameFi() {
        return municipalityNameFi;
    }

    public String getMunicipalityNameSv() {
        return municipalityNameSv;
    }
}
