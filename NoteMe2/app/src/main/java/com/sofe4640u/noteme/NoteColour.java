package com.sofe4640u.noteme;

public enum NoteColour {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255),
    PINK(255, 192, 203); // Define RGB for PINK

    private final int r;
    private final int g;
    private final int b;

    NoteColour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
