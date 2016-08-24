package fi.om.initiative.service;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RandomHashCreator {

    private static final Object lock = new Object();

    private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm";

    private static SecureRandom rnd = new SecureRandom();

    private static AtomicLong increment = new AtomicLong(0);

    public static String randomString( int len ) {
        synchronized (lock) {
            if (increment.incrementAndGet() % 100 == 0) {
                rnd.setSeed(rnd.generateSeed(512));
            }

            StringBuilder builder = new StringBuilder( len );
            for( int i = 0; i < len; i++ ) {
                builder.append( ALLOWED_CHARACTERS.charAt( rnd.nextInt(ALLOWED_CHARACTERS.length()) ) );
            }

            return builder.toString();
        }
    }

}
