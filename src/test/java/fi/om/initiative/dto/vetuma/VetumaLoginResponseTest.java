package fi.om.initiative.dto.vetuma;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

public class VetumaLoginResponseTest {

    private VetumaLoginResponse response = new VetumaLoginResponse();

    @Test
    public void Names_OK() {
        response.setSUBJECTDATA("ETUNIMI=Armas Oskari, SUKUNIMI=Aallontie");
        assertArrayEquals(response.getNames(), "Armas Oskari", "Aallontie");

        response.setSUBJECTDATA("ETUNIMI=Maija, SUKUNIMI=Sinisalo-Koskinen");
        assertArrayEquals(response.getNames(), "Maija", "Sinisalo-Koskinen");
    }
    
    @Test
    public void Names_Empty() {
        response.setSUBJECTDATA("ETUNIMI=, SUKUNIMI=");
        assertArrayEquals(response.getNames(), null, null);
    }
    
    @Test
    public void Names_Missing() {
        response.setSUBJECTDATA(null);
        assertArrayEquals(response.getNames(), null, null);
    }
    
    private static void assertArrayEquals(String[] actual, String... expected) {
        assertEquals(asList(expected), asList(actual));
    }
    
    @Test
    public void SSN_OK() {
        response.setEXTRADATA("HETU=010101-123N");
        assertEquals("010101-123N", response.getSsn());

        response.setEXTRADATA("HETU=010101A1230");
        assertEquals("010101A1230", response.getSsn());

        response.setEXTRADATA("HETU=010101a123b");
        assertEquals("010101A123B", response.getSsn());
    }
    
    @Test
    public void SSN_Empty() {
        response.setEXTRADATA("HETU=");
        assertNull(response.getSsn());
    }
    
    @Test
    public void SSN_Missing() {
        response.setEXTRADATA("");
        assertNull(response.getSsn());
    }
    
}
