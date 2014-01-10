package fi.om.initiative.service;

import com.google.common.base.Strings;
import com.mysema.commons.lang.Assert;
import fi.om.initiative.util.VetumaMACStringBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class EncryptionService {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String VETUMA_ENCODING = "ISO-8859-1";
    
    private String password;
    
    private String vetumaSharedSecret;
    
    private static final String MAC_DELIM = "&";
    
    private static final int DEFAULT_SECURE_RANDOM_RESEED_INTERVAL = 100;
    
    private static final int DEFAULT_ENCRYPTOR_POOL_SIZE = 100;

    /**
     * Reading and writing live data needs to be fast. Thus no additional key obfuscation iterations.
     * Password used here is long, random and encrypted in configuration, i.e. practically impossible
     * to crack using brute force.
     */
    private static final int DEFAULT_KEY_OBTENTION_ITERATIONS = 1;

    private static final String DEFAULT_ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    
    /**
     * Prepending encryptor identifier to encrypted value, allows us to easily change
     * active encryptor later while being still able to read old data.
     */
    private static final String DEFAULT_ENCRYPTOR_ID = "e:";

    private final int secureRandomReseedInterval;
    
    private SecureRandom secureRandom = newSecureRandom();
    
    private PooledPBEStringEncryptor aesEncryptor;
    
    private int secureRandomsGenerated = 0;
    
    public EncryptionService(String registeredUserSecret, String vetumaSharedSecret) {
        this(registeredUserSecret, vetumaSharedSecret, DEFAULT_SECURE_RANDOM_RESEED_INTERVAL, DEFAULT_ENCRYPTOR_POOL_SIZE);
    }
    
    public EncryptionService(String password, String vetumaSharedSecret, int secureRandomReseedInterval, int encryptorPoolSize) {
        if (Strings.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("registeredUserSecret was null or empty");
        }
        this.vetumaSharedSecret = vetumaSharedSecret;
        this.password = password;

        this.secureRandomReseedInterval = secureRandomReseedInterval;
        
        aesEncryptor = new PooledPBEStringEncryptor();
        aesEncryptor.setProvider(new BouncyCastleProvider());
        aesEncryptor.setAlgorithm(DEFAULT_ALGORITHM);
        aesEncryptor.setPassword(password);
        aesEncryptor.setKeyObtentionIterations(DEFAULT_KEY_OBTENTION_ITERATIONS);
        aesEncryptor.setPoolSize(encryptorPoolSize);
    }
    
    public String initiativeSupportHash(Long initiativeId, String ssn) {
        Assert.notNull(initiativeId, "initiativeId");
        Assert.hasText(ssn, "ssn");

        StringBuilder message = new StringBuilder(256)
        .append(initiativeId).append(MAC_DELIM)
        .append(ssn).append(MAC_DELIM)
        .append(password);

        return base64Encode(sha256(message.toString(), DEFAULT_ENCODING), DEFAULT_ENCODING);
    }
    
    public String encrypt(String message) {
        // Add encryptor id to encrypted message
        return new StringBuilder(message.length() * 2)
        .append(DEFAULT_ENCRYPTOR_ID)
        .append(aesEncryptor.encrypt(message)).toString();
    }
    
    public String decrypt(String encryptedMessage) {
        if (encryptedMessage.startsWith(DEFAULT_ENCRYPTOR_ID)) {
            // Remove encryptor id before decrypting
            return aesEncryptor.decrypt(encryptedMessage.substring(DEFAULT_ENCRYPTOR_ID.length()));
        } else {
            throw new IllegalArgumentException("Unable to decrypt message");
        }
    }
    
    public String registeredUserHash(String ssn) {
        Assert.hasText(ssn, "ssn");

        StringBuilder message = new StringBuilder(256)
        .append(ssn).append(MAC_DELIM)
        .append(password);
        
        return base64Encode(sha256(message.toString(), DEFAULT_ENCODING), DEFAULT_ENCODING);
    }

    public String vetumaMAC(String text) {
        // NOTE: This is ugly. Refactor into e.g. a sub class which is defined in profile
        if (vetumaSharedSecret == null) {
            throw new IllegalStateException("vetumaSharedSecret is null");
        }
        return toHex(sha256(text + vetumaSharedSecret + VetumaMACStringBuilder.DELIM, VETUMA_ENCODING));
    }
    
    public byte[] sha256(String text, String encoding) {
        if (Strings.isNullOrEmpty(text)) {
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(encoding));
            
            return digest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String base64Encode(byte[] bytes, String encoding) {
        try {
            return new String(Base64.encode(bytes), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String toHex(byte[] bytes) {
        final String digits = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            int bi = b & 0xff;
            sb.append(digits.charAt(bi >> 4));
            sb.append(digits.charAt(bi & 0xf));
        }
        return sb.toString();
    }
    
    /**
     * @param lengthInBytes multiples of 3 seem to work best (no padding '_' needed)
     * @return
     */
    public synchronized String randomToken(int lengthInBytes) {
        byte[] bytes = new byte[lengthInBytes];
        secureRandom.nextBytes(bytes);

        // Reseed for next call
        secureRandomsGenerated++;
        if (secureRandomsGenerated % secureRandomReseedInterval == 0) {
            setSecureRandom(newSecureRandom());
        }

        return emailSafeBase64Encode(bytes);
    }
    
    void setSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }
    
    
    
    public String emailSafeBase64Encode(byte[] bytes) {
        // Replace /, + and = of base64 with email-safe variants _, - and .
        char[] chars = base64Encode(bytes, DEFAULT_ENCODING).toCharArray();
        for (int i=0; i < chars.length; i++) {
            char ch = chars[i];
            switch (ch) {
            case '+':
                chars[i] = '-';
                break;
            case '=':
                chars[i] = '_';
                break;
            case '/':
                chars[i] = '.';
                break;
            }
        }
        return new String(chars);
    }
    
    static SecureRandom newSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

}
