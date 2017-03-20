package fi.om.initiative.conf.saml;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SamlUserLoaderTest {

    @Test
    public void first_not_null() {
        assertThat(SamlUserLoader.firstNotEmpty("first", "second").get(), is("first"));
        assertThat(SamlUserLoader.firstNotEmpty("   ", "second").get(), is("second"));
        assertThat(SamlUserLoader.firstNotEmpty("   ", null), is(Optional.empty()));

    }

}