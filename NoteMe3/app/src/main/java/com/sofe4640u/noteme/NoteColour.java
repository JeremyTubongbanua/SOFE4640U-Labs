package com.sofe4640u.noteme;

public enum NoteColour {
    RED(255, 99, 71),
    ORANGE(255, 165, 0),
    YELLOW(255, 255, 0),
    GREEN(50, 205, 50),
    BLUE(70, 130, 180),
    PURPLE(147, 112, 219),
    PINK(255, 105, 180),
    CYAN(0, 255, 255),
    MAGENTA(255, 0, 255),
    AMBER(255, 191, 0),
    LIME(191, 255, 0),
    TEAL(0, 128, 128),
    INDIGO(75, 0, 130);

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
