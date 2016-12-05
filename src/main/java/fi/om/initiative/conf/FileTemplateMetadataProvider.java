package fi.om.initiative.conf;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Map;

public class FileTemplateMetadataProvider extends AbstractReloadingMetadataProvider {

    private final String baseUrl;
    private Resource resource;
    private Map<String, String> values;

    public FileTemplateMetadataProvider(Resource resource, String publicCert, String baseUrl) {

        this.resource = resource;
        values = Maps.newHashMap();
        values.put("PUBLIC_CERT", publicCert);
        values.put("BASE_URL", baseUrl);
        this.baseUrl = baseUrl;

    }

    @Override
    protected String getMetadataIdentifier() {
        return baseUrl;
    }

    @Override
    protected byte[] fetchMetadata() throws MetadataProviderException {
        return getMetaAsString().getBytes();
    }

    public String getMetaAsString() {
        try (InputStream inputstream = resource.getInputStream()){
            StrSubstitutor strSubstitutor = new StrSubstitutor(values, "%(", ")");
            return strSubstitutor.replace(IOUtils.toString(inputstream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
