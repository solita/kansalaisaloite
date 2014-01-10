package fi.om.initiative.service;

import static fi.om.initiative.service.EncryptionService.newSecureRandom;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.security.SecureRandom;

import org.junit.Test;

public class EncryptionServiceTest {
    
    private static final int RESEED_RATE = 3;

    private EncryptionService encryptionService = new EncryptionService("registeredUserSecret", "vetumaSharedSecret", RESEED_RATE, 1); 
    
    /**
     * Ensure that SecureRandom is reseeded at specified rate (3)
     */
    @Test
    public void SecureRandom_Reseed() {
        int LEN = 6;
        byte[] seed = {1,2,3,4};

        // NOTE: SecureRandoms with same seed produce same results
        SecureRandom sr = newSecureRandom();
        sr.setSeed(seed);
        encryptionService.setSecureRandom(sr);

        sr = newSecureRandom();
        sr.setSeed(seed);
        

        byte[] buffer = new byte[LEN];
        for (int i=0; i < RESEED_RATE; i++) {
            sr.nextBytes(buffer);
            assertEquals(encryptionService.emailSafeBase64Encode(buffer), encryptionService.randomToken(LEN));
        }
        // RESEED
        sr.nextBytes(buffer);
        assertFalse(encryptionService.emailSafeBase64Encode(buffer).equals(encryptionService.randomToken(LEN)));
    }

    @Test
    public void Encryption_RoundTrip() {
        final String message = "abc DEF\n123\tåäöÅÄÖ";
        String encryptedMessage = encryptionService.encrypt(message);
     
        // Something is actually done to the message
        assertTrue(!encryptedMessage.equals(message));
        
        // Result varies for each call due to random salt
        assertTrue(!encryptedMessage.equals(encryptionService.encrypt(message)));
        
        // Decrypting produces the original message
        assertEquals(message, encryptionService.decrypt(encryptedMessage));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Encryption_Version() {
        final String message = "whatever";
        String encryptedMessage = encryptionService.encrypt(message);
        encryptedMessage = "non-existing-prefix:" + encryptedMessage;
        encryptionService.decrypt(encryptedMessage);
    }
}
