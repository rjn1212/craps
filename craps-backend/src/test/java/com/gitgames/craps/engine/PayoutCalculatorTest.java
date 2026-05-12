package com.gitgames.craps.engine;

import com.gitgames.craps.model.BetType;
import com.gitgames.craps.model.DiceRoll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayoutCalculatorTest {

    @Test
    void passLineWin() {
        assertEquals(50, PayoutCalculator.calculateWinPayout(BetType.PASS_LINE, 25, null, new DiceRoll(3, 4)));
    }

    @Test
    void dontPassWin() {
        assertEquals(20, PayoutCalculator.calculateWinPayout(BetType.DONT_PASS, 10, null, new DiceRoll(4, 3)));
    }

    @Test
    void passOdds4_10() {
        assertEquals(30, PayoutCalculator.calculateWinPayout(BetType.PASS_ODDS, 10, 4, new DiceRoll(2, 2)));
    }

    @Test
    void passOdds5_9() {
        assertEquals(25, PayoutCalculator.calculateWinPayout(BetType.PASS_ODDS, 10, 5, new DiceRoll(2, 3)));
    }

    @Test
    void passOdds6_8() {
        assertEquals(22, PayoutCalculator.calculateWinPayout(BetType.PASS_ODDS, 10, 6, new DiceRoll(3, 3)));
    }

    @Test
    void dontPassOdds4_10() {
        assertEquals(15, PayoutCalculator.calculateWinPayout(BetType.DONT_PASS_ODDS, 10, 4, new DiceRoll(3, 4)));
    }

    @Test
    void dontPassOdds5_9() {
        // 10 * 2/3 = 6 (integer), + 10 = 16
        assertEquals(16, PayoutCalculator.calculateWinPayout(BetType.DONT_PASS_ODDS, 10, 5, new DiceRoll(3, 4)));
    }

    @Test
    void dontPassOdds6_8() {
        // 6 * 5/6 = 5, + 6 = 11
        assertEquals(11, PayoutCalculator.calculateWinPayout(BetType.DONT_PASS_ODDS, 6, 6, new DiceRoll(3, 4)));
    }

    @Test
    void place4() {
        // 25 + 25*9/5 = 25 + 45 = 70
        assertEquals(70, PayoutCalculator.calculateWinPayout(BetType.PLACE_4, 25, null, new DiceRoll(2, 2)));
    }

    @Test
    void place5() {
        // 25 + 25*7/5 = 25 + 35 = 60
        assertEquals(60, PayoutCalculator.calculateWinPayout(BetType.PLACE_5, 25, null, new DiceRoll(2, 3)));
    }

    @Test
    void place6() {
        // 30 + 30*7/6 = 30 + 35 = 65
        assertEquals(65, PayoutCalculator.calculateWinPayout(BetType.PLACE_6, 30, null, new DiceRoll(3, 3)));
    }

    @Test
    void fieldRegular() {
        assertEquals(20, PayoutCalculator.calculateWinPayout(BetType.FIELD, 10, null, new DiceRoll(1, 3)));
    }

    @Test
    void field2Pays2to1() {
        // 10 + 10*2 = 30
        assertEquals(30, PayoutCalculator.calculateWinPayout(BetType.FIELD, 10, null, new DiceRoll(1, 1)));
    }

    @Test
    void field12Pays3to1() {
        // 10 + 10*3 = 40
        assertEquals(40, PayoutCalculator.calculateWinPayout(BetType.FIELD, 10, null, new DiceRoll(6, 6)));
    }

    @Test
    void anySeven() {
        assertEquals(50, PayoutCalculator.calculateWinPayout(BetType.ANY_SEVEN, 10, null, new DiceRoll(3, 4)));
    }

    @Test
    void anyCraps() {
        assertEquals(80, PayoutCalculator.calculateWinPayout(BetType.ANY_CRAPS, 10, null, new DiceRoll(1, 1)));
    }

    @Test
    void yo() {
        assertEquals(160, PayoutCalculator.calculateWinPayout(BetType.YO, 10, null, new DiceRoll(5, 6)));
    }

    @Test
    void aces() {
        assertEquals(310, PayoutCalculator.calculateWinPayout(BetType.ACES, 10, null, new DiceRoll(1, 1)));
    }

    @Test
    void boxcars() {
        assertEquals(310, PayoutCalculator.calculateWinPayout(BetType.BOXCARS, 10, null, new DiceRoll(6, 6)));
    }

    @Test
    void hard4() {
        assertEquals(80, PayoutCalculator.calculateWinPayout(BetType.HARD_4, 10, null, new DiceRoll(2, 2)));
    }

    @Test
    void hard6() {
        assertEquals(100, PayoutCalculator.calculateWinPayout(BetType.HARD_6, 10, null, new DiceRoll(3, 3)));
    }

    @Test
    void cAndECraps() {
        assertEquals(40, PayoutCalculator.calculateWinPayout(BetType.C_AND_E, 10, null, new DiceRoll(1, 1)));
    }

    @Test
    void cAndEYo() {
        assertEquals(80, PayoutCalculator.calculateWinPayout(BetType.C_AND_E, 10, null, new DiceRoll(5, 6)));
    }

    @Test
    void hornOn2() {
        // $4 bet, unit=$1. 1*30 + 1 - 3 = 28
        assertEquals(28, PayoutCalculator.calculateWinPayout(BetType.HORN, 4, null, new DiceRoll(1, 1)));
    }

    @Test
    void hornOn3() {
        // $4 bet, unit=$1. 1*15 + 1 - 3 = 13
        assertEquals(13, PayoutCalculator.calculateWinPayout(BetType.HORN, 4, null, new DiceRoll(1, 2)));
    }

    @Test
    void hornOn11() {
        assertEquals(13, PayoutCalculator.calculateWinPayout(BetType.HORN, 4, null, new DiceRoll(5, 6)));
    }

    @Test
    void hornOn12() {
        assertEquals(28, PayoutCalculator.calculateWinPayout(BetType.HORN, 4, null, new DiceRoll(6, 6)));
    }

    @Test
    void hornLoss() {
        assertEquals(0, PayoutCalculator.calculateWinPayout(BetType.HORN, 4, null, new DiceRoll(3, 4)));
    }
}
