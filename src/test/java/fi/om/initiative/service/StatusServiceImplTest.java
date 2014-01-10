package fi.om.initiative.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StatusServiceImplTest {

    @Test
    public void Formatted_BuildTimeStamp_OK() {
        String result = StatusServiceImpl.getFormattedBuildTimeStamp("20121126160649");
        assertTrue("2012-11-26 16:06:49".equals(result));
    }

    @Test
    public void Formatted_BuildTimeStamp_Missing() {
        String expected = "-";
        String result = StatusServiceImpl.getFormattedBuildTimeStamp("dev");
        assertTrue(expected.equals(result));

        result = StatusServiceImpl.getFormattedBuildTimeStamp("");
        assertTrue(expected.equals(result));

        result = StatusServiceImpl.getFormattedBuildTimeStamp(null);
        assertTrue(expected.equals(result));
    }
    
}
