package org.example.liquidsort;


public class Move {
    private final int fromTube;
    private final int toTube;

    public Move(int fromTube, int toTube) {
        this.fromTube = fromTube;
        this.toTube = toTube;
    }

    public int getFromTube() {
        return fromTube;
    }

    public int getToTube() {
        return toTube;
    }

    @Override
    public String toString() {
        return String.format("(%2d, %2d)", fromTube, toTube);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return fromTube == move.fromTube && toTube == move.toTube;
    }

    @Override
    public int hashCode() {
        return fromTube * 31 + toTube;
    }
}

