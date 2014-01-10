package fi.om.initiative.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LabelKeyTest {

    @Test
    public void Label_Keys() {
        assertEquals("initiative.field", FieldLabelKeyMethod.pathToLabelKey("initiative.field"));
        assertEquals("initiative.links.labels.fi", FieldLabelKeyMethod.pathToLabelKey("initiative.links[0].labels.fi"));
        assertEquals("initiative.links.labels.sv", FieldLabelKeyMethod.pathToLabelKey("initiative.links[mapKey].labels.sv"));
        assertEquals("initiative.links.uri", FieldLabelKeyMethod.pathToLabelKey("initiative.links[0].uri"));
        assertEquals("initiative.fi.eld", FieldLabelKeyMethod.pathToLabelKey("initiative.fi.eld"));
    }
}
