package ar.edu.iw3.util;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static Integer generateFourDigitRandom() {
        int min = 1000;
        int max = 9999;
        return secureRandom.nextInt((max - min) + 1) + min;
    }
}
