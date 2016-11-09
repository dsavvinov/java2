package net.responses;

public class StatResponseData {
    private final int[] parts;

    public StatResponseData(int[] parts) {
        this.parts = parts;
    }

    public int[] getParts() {
        return parts;
    }
}
