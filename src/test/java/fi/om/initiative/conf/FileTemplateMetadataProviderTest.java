package fi.om.initiative.conf;


import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class FileTemplateMetadataProviderTest {


    private final ClassPathResource REAL_TEMPLATE = new ClassPathResource("saml/our-metadata.xml");

    @Test
    public void replaces_template_with_values() {

        String replacedMeta = new FileTemplateMetadataProvider(REAL_TEMPLATE, "julkinen sertifikaatti", "http://localhost").getMetaAsString();


        assertThat(replacedMeta, containsString(
                "                    <ds:X509Certificate>\n" +
                        "                        julkinen sertifikaatti\n" +
                        "                    </ds:X509Certificate>"
        ));

        assertThat(replacedMeta, containsString(
                "<md:SingleLogoutService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" Location=\"http://localhost/saml/SingleLogout\"/>"));

        assertThat(replacedMeta, containsString(
                "<md:SingleLogoutService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"http://localhost/saml/SingleLogout\"/>"
        ));


    }

}