package net;

import java.util.Random;

public class PimpedRandom {
    static int nextInt() {
        return rng.nextInt();
    }

    static int nextInt(int bound) {
        return rng.nextInt(bound);
    }

    static long nextLong() {
        return rng.nextLong();
    }

    static boolean nextBoolean() {
        return rng.nextBoolean();
    }

    static String nextString() {
        char[] chars = new char[rng.nextInt(1000)];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) rng.nextInt(200);
        }
        return new String(chars);
    }

    static int[] nextIntArr() {
        int count = rng.nextInt(1000);
        return rng.ints(count).toArray();
    }

    static short nextShort() {
        return (short) nextInt();
    }

    static void nextBytes(byte[] bytes) {
        rng.nextBytes(bytes);
    }

    static byte[] nextBytes(int count) {
        byte[] res = new byte[count];
        nextBytes(res);
        return res;
    }

    private static final Random rng = new Random(42);

}
