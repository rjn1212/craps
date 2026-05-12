package com.gitgames.craps.engine;

import com.gitgames.craps.model.BetType;
import com.gitgames.craps.model.DiceRoll;

public final class PayoutCalculator {

    private PayoutCalculator() {}

    /**
     * Returns the total amount returned to the player on a winning bet.
     * This includes the original wager plus winnings.
     * Returns 0 for losses.
     */
    public static int calculateWinPayout(BetType type, int amount, Integer pointNumber, DiceRoll roll) {
        return switch (type) {
            case PASS_LINE, DONT_PASS, COME, DONT_COME -> amount * 2;

            case PASS_ODDS, COME_ODDS -> {
                int point = pointNumber != null ? pointNumber : 0;
                yield amount + passOddsWinnings(amount, point);
            }

            case DONT_PASS_ODDS, DONT_COME_ODDS -> {
                int point = pointNumber != null ? pointNumber : 0;
                yield amount + dontPassOddsWinnings(amount, point);
            }

            case PLACE_4, PLACE_10 -> amount + (amount * 9 / 5);
            case PLACE_5, PLACE_9 -> amount + (amount * 7 / 5);
            case PLACE_6, PLACE_8 -> amount + (amount * 7 / 6);

            case FIELD -> {
                int total = roll.total();
                if (total == 2) yield amount + amount * 2;       // 2:1
                else if (total == 12) yield amount + amount * 3; // 3:1
                else yield amount * 2;                           // 1:1
            }

            case ANY_SEVEN -> amount + amount * 4;       // 4:1
            case ANY_CRAPS -> amount + amount * 7;       // 7:1
            case YO -> amount + amount * 15;             // 15:1
            case ACES -> amount + amount * 30;           // 30:1
            case BOXCARS -> amount + amount * 30;        // 30:1

            case HARD_4, HARD_10 -> amount + amount * 7; // 7:1
            case HARD_6, HARD_8 -> amount + amount * 9;  // 9:1

            case C_AND_E -> {
                int total = roll.total();
                if (total == 2 || total == 3 || total == 12) yield amount + amount * 3; // 3:1 on craps
                else if (total == 11) yield amount + amount * 7;                        // 7:1 on yo
                else yield 0;
            }

            case HORN -> {
                int total = roll.total();
                int unitSize = amount / 4;
                if (total == 2 || total == 12) yield unitSize * 30 + unitSize - (unitSize * 3); // 30:1 on winning unit, lose 3 units
                else if (total == 3 || total == 11) yield unitSize * 15 + unitSize - (unitSize * 3); // 15:1 on winning unit, lose 3 units
                else yield 0;
            }
        };
    }

    private static int passOddsWinnings(int amount, int point) {
        return switch (point) {
            case 4, 10 -> amount * 2;      // 2:1
            case 5, 9 -> amount * 3 / 2;   // 3:2
            case 6, 8 -> amount * 6 / 5;   // 6:5
            default -> 0;
        };
    }

    private static int dontPassOddsWinnings(int amount, int point) {
        return switch (point) {
            case 4, 10 -> amount / 2;      // 1:2
            case 5, 9 -> amount * 2 / 3;   // 2:3
            case 6, 8 -> amount * 5 / 6;   // 5:6
            default -> 0;
        };
    }
}
