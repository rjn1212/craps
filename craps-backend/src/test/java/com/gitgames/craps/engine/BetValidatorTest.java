package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;
import com.gitgames.craps.service.InvalidBetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BetValidatorTest {

    private BetValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BetValidator();
    }

    private Game newGame() {
        return new Game(1000, new TableSettings(5, 500, TableSettings.ODDS_3_4_5X));
    }

    private Game gameInPointPhase(int point) {
        Game game = newGame();
        game.setRoundPhase(RoundPhase.POINT);
        game.setPoint(point);
        return game;
    }

    @Nested
    class PhaseRestrictions {

        @Test
        void passLineAllowedDuringComeOut() {
            assertDoesNotThrow(() -> validator.validate(newGame(), BetType.PASS_LINE, 25, null));
        }

        @Test
        void passLineBlockedDuringPoint() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(gameInPointPhase(6), BetType.PASS_LINE, 25, null));
        }

        @Test
        void dontPassAllowedDuringComeOut() {
            assertDoesNotThrow(() -> validator.validate(newGame(), BetType.DONT_PASS, 25, null));
        }

        @Test
        void dontPassBlockedDuringPoint() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(gameInPointPhase(6), BetType.DONT_PASS, 25, null));
        }

        @Test
        void comeBlockedDuringComeOut() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(newGame(), BetType.COME, 25, null));
        }

        @Test
        void comeAllowedDuringPoint() {
            assertDoesNotThrow(() -> validator.validate(gameInPointPhase(6), BetType.COME, 25, null));
        }

        @Test
        void dontComeBlockedDuringComeOut() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(newGame(), BetType.DONT_COME, 25, null));
        }

        @Test
        void dontComeAllowedDuringPoint() {
            assertDoesNotThrow(() -> validator.validate(gameInPointPhase(6), BetType.DONT_COME, 25, null));
        }
    }

    @Nested
    class TableLimits {

        @Test
        void belowMinimumRejected() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(newGame(), BetType.PASS_LINE, 3, null));
        }

        @Test
        void aboveMaximumRejected() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(newGame(), BetType.PASS_LINE, 600, null));
        }

        @Test
        void insufficientBankrollRejected() {
            Game game = new Game(10, new TableSettings(5, 500, TableSettings.ODDS_3_4_5X));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_LINE, 25, null));
        }
    }

    @Nested
    class DuplicateBets {

        @Test
        void duplicatePassLineRejected() {
            Game game = newGame();
            game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_LINE, 25, null));
        }

        @Test
        void duplicatePlaceBetRejected() {
            Game game = gameInPointPhase(6);
            game.addBet(new Bet(BetType.PLACE_8, 30, 0));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PLACE_8, 30, null));
        }

        @Test
        void duplicateHardwayRejected() {
            Game game = newGame();
            game.addBet(new Bet(BetType.HARD_6, 10, 0));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.HARD_6, 10, null));
        }
    }

    @Nested
    class OddsValidation {

        @Test
        void passOddsRequiresPassLine() {
            Game game = gameInPointPhase(4);
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_ODDS, 25, null));
        }

        @Test
        void passOddsWithinLimitAllowed() {
            Game game = gameInPointPhase(4);
            game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
            // 3-4-5x: 3x on 4 = 75 max
            assertDoesNotThrow(() -> validator.validate(game, BetType.PASS_ODDS, 75, null));
        }

        @Test
        void passOddsOverLimitRejected() {
            Game game = gameInPointPhase(4);
            game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
            // 3x on 4 = 75 max, 76 should fail
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_ODDS, 76, null));
        }

        @Test
        void passOdds5x_On6() {
            Game game = gameInPointPhase(6);
            game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
            // 5x on 6 = 125 max
            assertDoesNotThrow(() -> validator.validate(game, BetType.PASS_ODDS, 125, null));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_ODDS, 126, null));
        }

        @Test
        void passOdds4x_On5() {
            Game game = gameInPointPhase(5);
            game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
            // 4x on 5 = 100 max
            assertDoesNotThrow(() -> validator.validate(game, BetType.PASS_ODDS, 100, null));
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.PASS_ODDS, 101, null));
        }

        @Test
        void dontPassOddsRequiresDontPass() {
            Game game = gameInPointPhase(4);
            assertThrows(InvalidBetException.class,
                () -> validator.validate(game, BetType.DONT_PASS_ODDS, 25, null));
        }
    }

    @Nested
    class HornValidation {

        @Test
        void hornMustBeDivisibleBy4() {
            assertThrows(InvalidBetException.class,
                () -> validator.validate(newGame(), BetType.HORN, 5, null));
        }

        @Test
        void hornDivisibleBy4Allowed() {
            assertDoesNotThrow(() -> validator.validate(newGame(), BetType.HORN, 8, null));
        }
    }

    @Test
    void gameOverRejectsBets() {
        Game game = newGame();
        game.setGamePhase(GamePhase.GAME_OVER);
        assertThrows(InvalidBetException.class,
            () -> validator.validate(game, BetType.PASS_LINE, 25, null));
    }

    @Test
    void fieldAllowedAnyPhase() {
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.FIELD, 10, null));
        assertDoesNotThrow(() -> validator.validate(gameInPointPhase(6), BetType.FIELD, 10, null));
    }

    @Test
    void singleRollPropsAllowedAnyPhase() {
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.ANY_SEVEN, 10, null));
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.ANY_CRAPS, 10, null));
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.YO, 10, null));
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.ACES, 10, null));
        assertDoesNotThrow(() -> validator.validate(newGame(), BetType.BOXCARS, 10, null));
    }
}
