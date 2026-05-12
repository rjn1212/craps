package com.gitgames.craps.model;

public record DiceRoll(int die1, int die2) {

    public int total() {
        return die1 + die2;
    }

    public boolean isHardway() {
        return die1 == die2;
    }

    public boolean isCraps() {
        int t = total();
        return t == 2 || t == 3 || t == 12;
    }

    public boolean isNatural() {
        int t = total();
        return t == 7 || t == 11;
    }

    public boolean isPointNumber() {
        int t = total();
        return t == 4 || t == 5 || t == 6 || t == 8 || t == 9 || t == 10;
    }

    public boolean isSeven() {
        return total() == 7;
    }
}
