package com.gitgames.craps.model;

public enum BetType {
    PASS_LINE("Pass Line", false, true),
    DONT_PASS("Don't Pass", false, true),
    COME("Come", false, true),
    DONT_COME("Don't Come", false, true),
    PASS_ODDS("Pass Line Odds", false, false),
    DONT_PASS_ODDS("Don't Pass Odds", false, false),
    COME_ODDS("Come Odds", false, false),
    DONT_COME_ODDS("Don't Come Odds", false, false),
    PLACE_4("Place 4", false, false),
    PLACE_5("Place 5", false, false),
    PLACE_6("Place 6", false, false),
    PLACE_8("Place 8", false, false),
    PLACE_9("Place 9", false, false),
    PLACE_10("Place 10", false, false),
    FIELD("Field", true, false),
    ANY_SEVEN("Any 7", true, false),
    ANY_CRAPS("Any Craps", true, false),
    YO("Yo (11)", true, false),
    ACES("Aces (2)", true, false),
    BOXCARS("Boxcars (12)", true, false),
    HARD_4("Hard 4", false, false),
    HARD_6("Hard 6", false, false),
    HARD_8("Hard 8", false, false),
    HARD_10("Hard 10", false, false),
    C_AND_E("C & E", true, false),
    HORN("Horn", true, false);

    private final String displayName;
    private final boolean singleRoll;
    private final boolean contractBet;

    BetType(String displayName, boolean singleRoll, boolean contractBet) {
        this.displayName = displayName;
        this.singleRoll = singleRoll;
        this.contractBet = contractBet;
    }

    public String getDisplayName() { return displayName; }
    public boolean isSingleRoll() { return singleRoll; }
    public boolean isContractBet() { return contractBet; }

    public boolean isOddsBet() {
        return this == PASS_ODDS || this == DONT_PASS_ODDS
            || this == COME_ODDS || this == DONT_COME_ODDS;
    }

    public boolean isPlaceBet() {
        return this == PLACE_4 || this == PLACE_5 || this == PLACE_6
            || this == PLACE_8 || this == PLACE_9 || this == PLACE_10;
    }

    public boolean isHardway() {
        return this == HARD_4 || this == HARD_6 || this == HARD_8 || this == HARD_10;
    }

    public int placeNumber() {
        return switch (this) {
            case PLACE_4 -> 4;
            case PLACE_5 -> 5;
            case PLACE_6 -> 6;
            case PLACE_8 -> 8;
            case PLACE_9 -> 9;
            case PLACE_10 -> 10;
            default -> throw new IllegalStateException("Not a place bet: " + this);
        };
    }

    public int hardwayNumber() {
        return switch (this) {
            case HARD_4 -> 4;
            case HARD_6 -> 6;
            case HARD_8 -> 8;
            case HARD_10 -> 10;
            default -> throw new IllegalStateException("Not a hardway bet: " + this);
        };
    }
}
