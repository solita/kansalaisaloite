package fi.om.initiative.dto.vetuma;

import fi.om.initiative.conf.WebTestConfiguration;
import fi.om.initiative.dto.LocalizedString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebTestConfiguration.class})  //for messageSource
public class VTJDataTest {

    private final String missingMunicipalityNameFi = VTJData.MISSING_MUNICIPALITY_FI; // "Kotikunta ei tiedossa";
    private final String missingMunicipalityNameSv = VTJData.MISSING_MUNICIPALITY_SV; // "På svenska: Kotikunta ei tiedossa";
    
    @Test
    public void Parse_HST_Card_Missing_Municipality_Test_Data() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?><ns2:VTJHenkiloVastaussanoma versio=\"1.0\" sanomatunnus=\"PERUSJHHS2\" tietojenPoimintaaika=\"20121210154206\" xmlns:ns2=\"http://xml.vrk.fi/schema/vtjkysely\" xmlns=\"http://tempuri.org/\"><ns2:Asiakasinfo><ns2:InfoS>10.12.2012 15:42</ns2:InfoS><ns2:InfoR>10.12.2012 15:42</ns2:InfoR><ns2:InfoE>10.12.2012 15:42</ns2:InfoE></ns2:Asiakasinfo><ns2:Paluukoodi koodi=\"0000\">Haku onnistui</ns2:Paluukoodi><ns2:Hakuperusteet><ns2:Henkilotunnus hakuperusteTekstiE=\"Not used\" hakuperusteTekstiR=\"Beteckningen har inte användat\" hakuperusteTekstiS=\"Tunnushakuperustetta ei ole kaytetty\" hakuperustePaluukoodi=\"4\"></ns2:Henkilotunnus><ns2:SahkoinenAsiointitunnus hakuperusteTekstiE=\"Found\" hakuperusteTekstiR=\"Hittades\" hakuperusteTekstiS=\"Löytyi\" hakuperustePaluukoodi=\"1\">99755053X</ns2:SahkoinenAsiointitunnus></ns2:Hakuperusteet><ns2:Henkilo><ns2:Henkilotunnus voimassaolokoodi=\"1\">010673-998H</ns2:Henkilotunnus><ns2:NykyinenSukunimi><ns2:Sukunimi>Junnila</ns2:Sukunimi></ns2:NykyinenSukunimi><ns2:NykyisetEtunimet><ns2:Etunimet>Meiju Sanna-Maria Taika</ns2:Etunimet></ns2:NykyisetEtunimet><ns2:VakinainenKotimainenLahiosoite><ns2:LahiosoiteS></ns2:LahiosoiteS><ns2:LahiosoiteR></ns2:LahiosoiteR><ns2:Postinumero></ns2:Postinumero><ns2:PostitoimipaikkaS></ns2:PostitoimipaikkaS><ns2:PostitoimipaikkaR></ns2:PostitoimipaikkaR><ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenKotimainenLahiosoite><ns2:VakinainenUlkomainenLahiosoite><ns2:UlkomainenLahiosoite></ns2:UlkomainenLahiosoite><ns2:UlkomainenPaikkakuntaJaValtioS></ns2:UlkomainenPaikkakuntaJaValtioS><ns2:UlkomainenPaikkakuntaJaValtioR></ns2:UlkomainenPaikkakuntaJaValtioR><ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen></ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen><ns2:Valtiokoodi3></ns2:Valtiokoodi3><ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenUlkomainenLahiosoite><ns2:Kotikunta><ns2:Kuntanumero></ns2:Kuntanumero><ns2:KuntaS></ns2:KuntaS><ns2:KuntaR></ns2:KuntaR><ns2:KuntasuhdeAlkupvm></ns2:KuntasuhdeAlkupvm></ns2:Kotikunta><ns2:Kuolintiedot><ns2:Kuolinpvm></ns2:Kuolinpvm></ns2:Kuolintiedot><ns2:Aidinkieli><ns2:Kielikoodi>fi</ns2:Kielikoodi><ns2:KieliS>suomi</ns2:KieliS><ns2:KieliR>finska</ns2:KieliR><ns2:KieliSelvakielinen></ns2:KieliSelvakielinen></ns2:Aidinkieli><ns2:SuomenKansalaisuusTietokoodi>1</ns2:SuomenKansalaisuusTietokoodi></ns2:Henkilo></ns2:VTJHenkiloVastaussanoma>";
        VTJData vtjData = VTJData.parse(xml);
        assertEquals("Meiju Sanna-Maria Taika", vtjData.getFirstNames());
        assertEquals("Junnila", vtjData.getLastName());
        assertTrue(vtjData.isFinnishCitizen());
        assertFalse(vtjData.isDead());
        
        LocalizedString municipality = vtjData.getHomeMunicipality();
        assertEquals(missingMunicipalityNameFi, municipality.getFi());
        assertEquals(missingMunicipalityNameSv, municipality.getSv());
        assertEquals(null, vtjData.getMunicipalityCode());
        assertEquals("Haku onnistui", vtjData.getReturnCodeDescription());
    }
    
    @Test
    public void Parse_HST_Card_Living_Abroad_Test_Data() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?><ns2:VTJHenkiloVastaussanoma versio=\"1.0\" sanomatunnus=\"PERUSJHHS2\" tietojenPoimintaaika=\"20121205125039\" xmlns:ns2=\"http://xml.vrk.fi/schema/vtjkysely\" xmlns=\"http://tempuri.org/\"><ns2:Asiakasinfo><ns2:InfoS>05.12.2012 12:50</ns2:InfoS><ns2:InfoR>05.12.2012 12:50</ns2:InfoR><ns2:InfoE>05.12.2012 12:50</ns2:InfoE></ns2:Asiakasinfo><ns2:Paluukoodi koodi=\"0000\">Haku onnistui</ns2:Paluukoodi><ns2:Hakuperusteet><ns2:Henkilotunnus hakuperusteTekstiE=\"Not used\" hakuperusteTekstiR=\"Beteckningen har inte användat\" hakuperusteTekstiS=\"Tunnushakuperustetta ei ole kaytetty\" hakuperustePaluukoodi=\"4\"></ns2:Henkilotunnus><ns2:SahkoinenAsiointitunnus hakuperusteTekstiE=\"Found\" hakuperusteTekstiR=\"Hittades\" hakuperusteTekstiS=\"Löytyi\" hakuperustePaluukoodi=\"1\">99755050U</ns2:SahkoinenAsiointitunnus></ns2:Hakuperusteet><ns2:Henkilo><ns2:Henkilotunnus voimassaolokoodi=\"1\">160864-999H</ns2:Henkilotunnus><ns2:NykyinenSukunimi><ns2:Sukunimi>Sutinen</ns2:Sukunimi></ns2:NykyinenSukunimi><ns2:NykyisetEtunimet><ns2:Etunimet>Sigvard Jonathan</ns2:Etunimet></ns2:NykyisetEtunimet><ns2:VakinainenKotimainenLahiosoite><ns2:LahiosoiteS></ns2:LahiosoiteS><ns2:LahiosoiteR></ns2:LahiosoiteR><ns2:Postinumero></ns2:Postinumero><ns2:PostitoimipaikkaS></ns2:PostitoimipaikkaS><ns2:PostitoimipaikkaR></ns2:PostitoimipaikkaR><ns2:AsuminenAlkupvm>19640816</ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenKotimainenLahiosoite><ns2:VakinainenUlkomainenLahiosoite><ns2:UlkomainenLahiosoite></ns2:UlkomainenLahiosoite><ns2:UlkomainenPaikkakuntaJaValtioS></ns2:UlkomainenPaikkakuntaJaValtioS><ns2:UlkomainenPaikkakuntaJaValtioR></ns2:UlkomainenPaikkakuntaJaValtioR><ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen></ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen><ns2:Valtiokoodi3></ns2:Valtiokoodi3><ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenUlkomainenLahiosoite><ns2:Kotikunta><ns2:Kuntanumero>200</ns2:Kuntanumero><ns2:KuntaS>Ulkomaat</ns2:KuntaS><ns2:KuntaR>Utlandet</ns2:KuntaR><ns2:KuntasuhdeAlkupvm>19640816</ns2:KuntasuhdeAlkupvm></ns2:Kotikunta><ns2:Kuolintiedot><ns2:Kuolinpvm></ns2:Kuolinpvm></ns2:Kuolintiedot><ns2:Aidinkieli><ns2:Kielikoodi>fi</ns2:Kielikoodi><ns2:KieliS>suomi</ns2:KieliS><ns2:KieliR>finska</ns2:KieliR><ns2:KieliSelvakielinen></ns2:KieliSelvakielinen></ns2:Aidinkieli><ns2:SuomenKansalaisuusTietokoodi>1</ns2:SuomenKansalaisuusTietokoodi></ns2:Henkilo></ns2:VTJHenkiloVastaussanoma>";
        VTJData vtjData = VTJData.parse(xml);
        assertEquals("Sigvard Jonathan", vtjData.getFirstNames());
        assertEquals("Sutinen", vtjData.getLastName());
        assertTrue(vtjData.isFinnishCitizen());
        assertFalse(vtjData.isDead());
        
        LocalizedString municipality = vtjData.getHomeMunicipality();
        assertEquals("Ulkomaat", municipality.getFi());
        assertEquals("Utlandet", municipality.getSv());
        assertEquals("200", vtjData.getMunicipalityCode());
        assertEquals("Haku onnistui", vtjData.getReturnCodeDescription());
    }
    
    @Test
    public void Parse_OP_Test_Data() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?><ns2:VTJHenkiloVastaussanoma versio=\"1.0\" sanomatunnus=\"PERUSJHHS2\" tietojenPoimintaaika=\"20120801092526\" xmlns:ns2=\"http://xml.vrk.fi/schema/vtjkysely\" xmlns=\"http://tempuri.org/\"><ns2:Asiakasinfo><ns2:InfoS>01.08.2012 09:25</ns2:InfoS><ns2:InfoR>01.08.2012 09:25</ns2:InfoR><ns2:InfoE>01.08.2012 09:25</ns2:InfoE></ns2:Asiakasinfo><ns2:Paluukoodi koodi=\"0000\">Haku onnistui</ns2:Paluukoodi><ns2:Hakuperusteet><ns2:Henkilotunnus hakuperusteTekstiE=\"Found\" hakuperusteTekstiR=\"Hittades\" hakuperusteTekstiS=\"Löytyi\" hakuperustePaluukoodi=\"1\">081181-9984</ns2:Henkilotunnus><ns2:SahkoinenAsiointitunnus hakuperusteTekstiE=\"Not used\" hakuperusteTekstiR=\"Beteckningen har inte användat\" hakuperusteTekstiS=\"Tunnushakuperustetta ei ole kaytetty\" hakuperustePaluukoodi=\"4\"></ns2:SahkoinenAsiointitunnus></ns2:Hakuperusteet><ns2:Henkilo><ns2:Henkilotunnus voimassaolokoodi=\"1\">081181-9984</ns2:Henkilotunnus><ns2:NykyinenSukunimi><ns2:Sukunimi>Marttila</ns2:Sukunimi></ns2:NykyinenSukunimi><ns2:NykyisetEtunimet><ns2:Etunimet>Sylvi Sofie</ns2:Etunimet></ns2:NykyisetEtunimet><ns2:VakinainenKotimainenLahiosoite><ns2:LahiosoiteS>Sepänkatu 11 A 5</ns2:LahiosoiteS><ns2:LahiosoiteR></ns2:LahiosoiteR><ns2:Postinumero>70100</ns2:Postinumero><ns2:PostitoimipaikkaS>KUOPIO</ns2:PostitoimipaikkaS><ns2:PostitoimipaikkaR>KUOPIO</ns2:PostitoimipaikkaR><ns2:AsuminenAlkupvm>20050525</ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenKotimainenLahiosoite><ns2:VakinainenUlkomainenLahiosoite><ns2:UlkomainenLahiosoite></ns2:UlkomainenLahiosoite><ns2:UlkomainenPaikkakuntaJaValtioS></ns2:UlkomainenPaikkakuntaJaValtioS><ns2:UlkomainenPaikkakuntaJaValtioR></ns2:UlkomainenPaikkakuntaJaValtioR><ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen></ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen><ns2:Valtiokoodi3></ns2:Valtiokoodi3><ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm><ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm></ns2:VakinainenUlkomainenLahiosoite><ns2:Kotikunta><ns2:Kuntanumero>297</ns2:Kuntanumero><ns2:KuntaS>Kuopio</ns2:KuntaS><ns2:KuntaR>Kuopiå</ns2:KuntaR><ns2:KuntasuhdeAlkupvm>20050525</ns2:KuntasuhdeAlkupvm></ns2:Kotikunta><ns2:Kuolintiedot><ns2:Kuolinpvm></ns2:Kuolinpvm></ns2:Kuolintiedot><ns2:Aidinkieli><ns2:Kielikoodi>fi</ns2:Kielikoodi><ns2:KieliS>suomi</ns2:KieliS><ns2:KieliR>finska</ns2:KieliR><ns2:KieliSelvakielinen></ns2:KieliSelvakielinen></ns2:Aidinkieli><ns2:SuomenKansalaisuusTietokoodi>1</ns2:SuomenKansalaisuusTietokoodi></ns2:Henkilo></ns2:VTJHenkiloVastaussanoma>\n";
        VTJData vtjData = VTJData.parse(xml);
        assertEquals("Sylvi Sofie", vtjData.getFirstNames());
        assertEquals("Marttila", vtjData.getLastName());
        assertTrue(vtjData.isFinnishCitizen());
        assertFalse(vtjData.isDead());
        
        LocalizedString municipality = vtjData.getHomeMunicipality();
//        assertEquals("297", municipality.getCode());
        assertEquals("Kuopio", municipality.getFi());
        assertEquals("Kuopiå", municipality.getSv());
    }
    
    @Test
    public void Parse_Nordea_Test_Data() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n" + 
        		"<ns2:VTJHenkiloVastaussanoma versio=\"1.0\"\n" + 
        		"    sanomatunnus=\"PERUSJHHS2\" tietojenPoimintaaika=\"20120813104258\"\n" + 
        		"    xmlns:ns2=\"http://xml.vrk.fi/schema/vtjkysely\" xmlns=\"http://tempuri.org/\">\n" + 
        		"    <ns2:Asiakasinfo>\n" + 
        		"        <ns2:InfoS>13.08.2012 10:42</ns2:InfoS>\n" + 
        		"        <ns2:InfoR>13.08.2012 10:42</ns2:InfoR>\n" + 
        		"        <ns2:InfoE>13.08.2012 10:42</ns2:InfoE>\n" + 
        		"    </ns2:Asiakasinfo>\n" + 
        		"    <ns2:Paluukoodi koodi=\"0000\">Haku onnistui</ns2:Paluukoodi>\n" + 
        		"    <ns2:Hakuperusteet>\n" + 
        		"        <ns2:Henkilotunnus hakuperusteTekstiE=\"Found\"\n" + 
        		"            hakuperusteTekstiR=\"Hittades\" hakuperusteTekstiS=\"Löytyi\"\n" + 
        		"            hakuperustePaluukoodi=\"1\">210281-9988</ns2:Henkilotunnus>\n" + 
        		"        <ns2:SahkoinenAsiointitunnus\n" + 
        		"            hakuperusteTekstiE=\"Not used\" hakuperusteTekstiR=\"Beteckningen har inte användat\"\n" + 
        		"            hakuperusteTekstiS=\"Tunnushakuperustetta ei ole kaytetty\"\n" + 
        		"            hakuperustePaluukoodi=\"4\"></ns2:SahkoinenAsiointitunnus>\n" + 
        		"    </ns2:Hakuperusteet>\n" + 
        		"    <ns2:Henkilo>\n" + 
        		"        <ns2:Henkilotunnus voimassaolokoodi=\"1\">210281-9988\n" + 
        		"        </ns2:Henkilotunnus>\n" + 
        		"        <ns2:NykyinenSukunimi>\n" + 
        		"            <ns2:Sukunimi>Demo</ns2:Sukunimi>\n" + 
        		"        </ns2:NykyinenSukunimi>\n" + 
        		"        <ns2:NykyisetEtunimet>\n" + 
        		"            <ns2:Etunimet>Nordea</ns2:Etunimet>\n" + 
        		"        </ns2:NykyisetEtunimet>\n" + 
        		"        <ns2:VakinainenKotimainenLahiosoite>\n" + 
        		"            <ns2:LahiosoiteS></ns2:LahiosoiteS>\n" + 
        		"            <ns2:LahiosoiteR></ns2:LahiosoiteR>\n" + 
        		"            <ns2:Postinumero></ns2:Postinumero>\n" + 
        		"            <ns2:PostitoimipaikkaS></ns2:PostitoimipaikkaS>\n" + 
        		"            <ns2:PostitoimipaikkaR></ns2:PostitoimipaikkaR>\n" + 
        		"            <ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm>\n" + 
        		"            <ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm>\n" + 
        		"        </ns2:VakinainenKotimainenLahiosoite>\n" + 
        		"        <ns2:VakinainenUlkomainenLahiosoite>\n" + 
        		"            <ns2:UlkomainenLahiosoite></ns2:UlkomainenLahiosoite>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioS></ns2:UlkomainenPaikkakuntaJaValtioS>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioR></ns2:UlkomainenPaikkakuntaJaValtioR>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen></ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen>\n" + 
        		"            <ns2:Valtiokoodi3></ns2:Valtiokoodi3>\n" + 
        		"            <ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm>\n" + 
        		"            <ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm>\n" + 
        		"        </ns2:VakinainenUlkomainenLahiosoite>\n" + 
        		"        <ns2:Kotikunta>\n" + 
        		"            <ns2:Kuntanumero></ns2:Kuntanumero>\n" + 
        		"            <ns2:KuntaS></ns2:KuntaS>\n" + 
        		"            <ns2:KuntaR></ns2:KuntaR>\n" + 
        		"            <ns2:KuntasuhdeAlkupvm></ns2:KuntasuhdeAlkupvm>\n" + 
        		"        </ns2:Kotikunta>\n" + 
        		"        <ns2:Kuolintiedot>\n" + 
        		"            <ns2:Kuolinpvm></ns2:Kuolinpvm>\n" + 
        		"        </ns2:Kuolintiedot>\n" + 
        		"        <ns2:Aidinkieli>\n" + 
        		"            <ns2:Kielikoodi></ns2:Kielikoodi>\n" + 
        		"            <ns2:KieliS></ns2:KieliS>\n" + 
        		"            <ns2:KieliR></ns2:KieliR>\n" + 
        		"            <ns2:KieliSelvakielinen></ns2:KieliSelvakielinen>\n" + 
        		"        </ns2:Aidinkieli>\n" + 
        		"        <ns2:SuomenKansalaisuusTietokoodi>0\n" + 
        		"        </ns2:SuomenKansalaisuusTietokoodi>\n" + 
        		"    </ns2:Henkilo>\n" + 
        		"</ns2:VTJHenkiloVastaussanoma>";
        VTJData vtjData = VTJData.parse(xml);
        assertEquals("Nordea", vtjData.getFirstNames());
        assertEquals("Demo", vtjData.getLastName());
        assertFalse(vtjData.isFinnishCitizen());
        assertFalse(vtjData.isDead());
        
        LocalizedString municipality = vtjData.getHomeMunicipality();
//        assertNull(municipality);
        assertEquals(missingMunicipalityNameFi, municipality.getFi());
        assertEquals(missingMunicipalityNameSv, municipality.getSv());
        assertEquals(null, vtjData.getMunicipalityCode());
    }
    
    @Test
    public void Parse_Handelsbanken_Dead() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n" + 
        		"<ns2:VTJHenkiloVastaussanoma versio=\"1.0\"\n" + 
        		"    sanomatunnus=\"PERUSJHHS2\" tietojenPoimintaaika=\"20120813104609\"\n" + 
        		"    xmlns:ns2=\"http://xml.vrk.fi/schema/vtjkysely\" xmlns=\"http://tempuri.org/\">\n" + 
        		"    <ns2:Asiakasinfo>\n" + 
        		"        <ns2:InfoS>13.08.2012 10:46</ns2:InfoS>\n" + 
        		"        <ns2:InfoR>13.08.2012 10:46</ns2:InfoR>\n" + 
        		"        <ns2:InfoE>13.08.2012 10:46</ns2:InfoE>\n" + 
        		"    </ns2:Asiakasinfo>\n" + 
        		"    <ns2:Paluukoodi koodi=\"0000\">Haku onnistui</ns2:Paluukoodi>\n" + 
        		"    <ns2:Hakuperusteet>\n" + 
        		"        <ns2:Henkilotunnus hakuperusteTekstiE=\"Found\"\n" + 
        		"            hakuperusteTekstiR=\"Hittades\" hakuperusteTekstiS=\"Löytyi\"\n" + 
        		"            hakuperustePaluukoodi=\"1\">010101-123N</ns2:Henkilotunnus>\n" + 
        		"        <ns2:SahkoinenAsiointitunnus\n" + 
        		"            hakuperusteTekstiE=\"Not used\" hakuperusteTekstiR=\"Beteckningen har inte användat\"\n" + 
        		"            hakuperusteTekstiS=\"Tunnushakuperustetta ei ole kaytetty\"\n" + 
        		"            hakuperustePaluukoodi=\"4\"></ns2:SahkoinenAsiointitunnus>\n" + 
        		"    </ns2:Hakuperusteet>\n" + 
        		"    <ns2:Henkilo>\n" + 
        		"        <ns2:Henkilotunnus voimassaolokoodi=\"1\">010101-123N\n" + 
        		"        </ns2:Henkilotunnus>\n" + 
        		"        <ns2:NykyinenSukunimi>\n" + 
        		"            <ns2:Sukunimi>Testaaja</ns2:Sukunimi>\n" + 
        		"        </ns2:NykyinenSukunimi>\n" + 
        		"        <ns2:NykyisetEtunimet>\n" + 
        		"            <ns2:Etunimet>Teemu</ns2:Etunimet>\n" + 
        		"        </ns2:NykyisetEtunimet>\n" + 
        		"        <ns2:VakinainenKotimainenLahiosoite>\n" + 
        		"            <ns2:LahiosoiteS></ns2:LahiosoiteS>\n" + 
        		"            <ns2:LahiosoiteR></ns2:LahiosoiteR>\n" + 
        		"            <ns2:Postinumero></ns2:Postinumero>\n" + 
        		"            <ns2:PostitoimipaikkaS></ns2:PostitoimipaikkaS>\n" + 
        		"            <ns2:PostitoimipaikkaR></ns2:PostitoimipaikkaR>\n" + 
        		"            <ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm>\n" + 
        		"            <ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm>\n" + 
        		"        </ns2:VakinainenKotimainenLahiosoite>\n" + 
        		"        <ns2:VakinainenUlkomainenLahiosoite>\n" + 
        		"            <ns2:UlkomainenLahiosoite></ns2:UlkomainenLahiosoite>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioS></ns2:UlkomainenPaikkakuntaJaValtioS>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioR></ns2:UlkomainenPaikkakuntaJaValtioR>\n" + 
        		"            <ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen></ns2:UlkomainenPaikkakuntaJaValtioSelvakielinen>\n" + 
        		"            <ns2:Valtiokoodi3></ns2:Valtiokoodi3>\n" + 
        		"            <ns2:AsuminenAlkupvm></ns2:AsuminenAlkupvm>\n" + 
        		"            <ns2:AsuminenLoppupvm></ns2:AsuminenLoppupvm>\n" + 
        		"        </ns2:VakinainenUlkomainenLahiosoite>\n" + 
        		"        <ns2:Kotikunta>\n" + 
        		"            <ns2:Kuntanumero></ns2:Kuntanumero>\n" + 
        		"            <ns2:KuntaS></ns2:KuntaS>\n" + 
        		"            <ns2:KuntaR></ns2:KuntaR>\n" + 
        		"            <ns2:KuntasuhdeAlkupvm></ns2:KuntasuhdeAlkupvm>\n" + 
        		"        </ns2:Kotikunta>\n" + 
        		"        <ns2:Kuolintiedot>\n" + 
        		"            <ns2:Kuolinpvm>20120813</ns2:Kuolinpvm>\n" + 
        		"        </ns2:Kuolintiedot>\n" + 
        		"        <ns2:Aidinkieli>\n" + 
        		"            <ns2:Kielikoodi></ns2:Kielikoodi>\n" + 
        		"            <ns2:KieliS></ns2:KieliS>\n" + 
        		"            <ns2:KieliR></ns2:KieliR>\n" + 
        		"            <ns2:KieliSelvakielinen></ns2:KieliSelvakielinen>\n" + 
        		"        </ns2:Aidinkieli>\n" + 
        		"        <ns2:SuomenKansalaisuusTietokoodi>0\n" + 
        		"        </ns2:SuomenKansalaisuusTietokoodi>\n" + 
        		"    </ns2:Henkilo>\n" + 
        		"</ns2:VTJHenkiloVastaussanoma>";
        
        VTJData vtjData = VTJData.parse(xml);
        assertEquals("Teemu", vtjData.getFirstNames());
        assertEquals("Testaaja", vtjData.getLastName());
        assertFalse(vtjData.isFinnishCitizen());
        assertTrue(vtjData.isDead());
        
        LocalizedString municipality = vtjData.getHomeMunicipality();
//        assertNull(municipality);
        assertEquals(missingMunicipalityNameFi, municipality.getFi());
        assertEquals(missingMunicipalityNameSv, municipality.getSv());
        assertEquals(null, vtjData.getMunicipalityCode());
    }
}
