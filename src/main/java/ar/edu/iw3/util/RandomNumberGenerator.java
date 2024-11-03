package ar.edu.iw3.util;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static Integer generateFiveDigitRandom() {
        int min = 10000;
        int max = 99999;
        return secureRandom.nextInt((max - min) + 1) + min;
    }
}
