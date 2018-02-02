package fi.om.initiative.conf.saml;

import java.util.List;
import java.util.stream.Collectors;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.CollectionKeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.context.SAMLMessageContext;

/**
 * SAML key handler which can use multiple keys.
 */
public final class MultiKeyDecrypterSAMLContextProvider extends SAMLContextProviderLB {
	
	@Override
	protected void populateDecrypter(SAMLMessageContext samlContext) {
		super.populateDecrypter(samlContext);
		
		List<Credential> allCredentials = keyManager.getAvailableCredentials().stream()
				.map(cred -> keyManager.getCredential(cred))
				.collect(Collectors.toList());
	            	KeyInfoCredentialResolver resolver = new CollectionKeyInfoCredentialResolver(allCredentials);
	            	samlContext.getLocalDecrypter().setKEKResolver(resolver);
	}	
}

