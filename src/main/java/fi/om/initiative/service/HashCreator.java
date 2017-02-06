package fi.om.initiative.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class HashCreator {

    private final String hash;

    public HashCreator(String hash) {
        this.hash = hash;
    }

    public String hash(Long id) {
        return toShortSha1(id.toString() + hash);
    }

    public String hash(String value) {
        return toSha1(hash + value);
    }

    public boolean isHash(Long id, String expectedHash) {
        return hash(id).equals(expectedHash);
    }

    public boolean isHash(String value, String expectedHash) {
        return hash(value).equals(expectedHash);
    }

    public boolean isNotHash(Long id, String expectedHash) {
        return !isHash(id, expectedHash);
    }

    private static String toShortSha1(String password)
    {
        return toSha1(password).substring(0, 12);
    }

    private static String toSha1(String password) {
        String sha1;
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

}
