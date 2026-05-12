package com.gitgames.craps.model;

public record TableSettings(int minBet, int maxBet, int maxOddsMultiple) {

    public static final int ODDS_3_4_5X = 345;

    public int oddsMultipleForPoint(int point) {
        if (maxOddsMultiple == ODDS_3_4_5X) {
            return switch (point) {
                case 4, 10 -> 3;
                case 5, 9 -> 4;
                case 6, 8 -> 5;
                default -> 0;
            };
        }
        return maxOddsMultiple;
    }
}
