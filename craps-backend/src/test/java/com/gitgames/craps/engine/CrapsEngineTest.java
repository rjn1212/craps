package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrapsEngineTest {

    private CrapsEngine engine;

    @BeforeEach
    void setUp() {
        engine = new CrapsEngine();
    }

    private Game newGame() {
        return new Game(1000, new TableSettings(5, 500, TableSettings.ODDS_3_4_5X));
    }

    @Test
    void rollDiceProducesValidRange() {
        for (int i = 0; i < 100; i++) {
            DiceRoll roll = engine.rollDice();
            assertTrue(roll.die1() >= 1 && roll.die1() <= 6);
            assertTrue(roll.die2() >= 1 && roll.die2() <= 6);
            assertTrue(roll.total() >= 2 && roll.total() <= 12);
        }
    }

    @Test
    void comeOutNaturalWinsPassLine() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));

        RollRecord record = engine.resolveRoll(game, new DiceRoll(3, 4));

        assertEquals(RoundPhase.COME_OUT, record.phaseBefore());
        assertEquals(RoundPhase.COME_OUT, record.phaseAfter());
        assertNull(record.pointAfter());
        assertEquals(1, record.resolutions().size());
        assertEquals(BetStatus.WON, record.resolutions().get(0).outcome());
        assertEquals(1025, game.getBankroll());
        assertTrue(game.getActiveBets().isEmpty());
    }

    @Test
    void comeOutCrapsLosesPassLine() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));

        engine.resolveRoll(game, new DiceRoll(1, 1));

        assertEquals(975, game.getBankroll());
        assertTrue(game.getActiveBets().isEmpty());
    }

    @Test
    void comeOutPointEstablished() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));

        RollRecord record = engine.resolveRoll(game, new DiceRoll(2, 4));

        assertEquals(RoundPhase.COME_OUT, record.phaseBefore());
        assertEquals(RoundPhase.POINT, record.phaseAfter());
        assertEquals(6, record.pointAfter());
        assertEquals(RoundPhase.POINT, game.getRoundPhase());
        assertEquals(6, game.getPoint());
        assertTrue(record.resolutions().isEmpty());
        assertEquals(1, game.getActiveBets().size());
        assertEquals(6, game.getActiveBets().get(0).getPointNumber());
    }

    @Test
    void pointMadeWinsPassLine() {
        Game game = newGame();
        Bet passLine = new Bet(BetType.PASS_LINE, 25, 0);
        game.addBet(passLine);
        engine.resolveRoll(game, new DiceRoll(2, 4)); // establish point 6

        RollRecord record = engine.resolveRoll(game, new DiceRoll(3, 3)); // hard 6

        assertEquals(RoundPhase.POINT, record.phaseBefore());
        assertEquals(RoundPhase.COME_OUT, record.phaseAfter());
        assertNull(record.pointAfter());
        assertEquals(1, record.resolutions().size());
        assertEquals(BetStatus.WON, record.resolutions().get(0).outcome());
        assertEquals(1025, game.getBankroll());
    }

    @Test
    void sevenOutLosesPassLine() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
        engine.resolveRoll(game, new DiceRoll(2, 4)); // establish point 6

        engine.resolveRoll(game, new DiceRoll(3, 4)); // seven out

        assertEquals(RoundPhase.COME_OUT, game.getRoundPhase());
        assertNull(game.getPoint());
        assertEquals(975, game.getBankroll());
    }

    @Test
    void comeEstablishesPersonalPoint() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
        engine.resolveRoll(game, new DiceRoll(2, 4)); // establish point 6

        game.addBet(new Bet(BetType.COME, 25, 1));
        engine.resolveRoll(game, new DiceRoll(4, 1)); // come bet gets point 5

        Bet comeBet = game.getActiveBets().stream()
            .filter(b -> b.getType() == BetType.COME)
            .findFirst().orElse(null);
        assertNotNull(comeBet);
        assertEquals(5, comeBet.getPointNumber());
    }

    @Test
    void placeBetsToggledOffOnTransitionToComeOut() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
        engine.resolveRoll(game, new DiceRoll(2, 4)); // point 6

        game.addBet(new Bet(BetType.PLACE_8, 30, 1));
        assertTrue(game.getActiveBets().stream()
            .filter(b -> b.getType() == BetType.PLACE_8)
            .findFirst().get().isOn());

        engine.resolveRoll(game, new DiceRoll(3, 3)); // point made, back to come-out

        Bet placeBet = game.getActiveBets().stream()
            .filter(b -> b.getType() == BetType.PLACE_8)
            .findFirst().orElse(null);
        assertNotNull(placeBet);
        assertFalse(placeBet.isOn());
    }

    @Test
    void gameOverWhenBroke() {
        Game game = new Game(25, new TableSettings(5, 500, TableSettings.ODDS_3_4_5X));
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));

        engine.resolveRoll(game, new DiceRoll(1, 1)); // craps, lose 25

        assertEquals(GamePhase.GAME_OVER, game.getGamePhase());
        assertEquals("broke", game.getGameOverReason());
    }

    @Test
    void multipleResolutionsInOneRoll() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));
        game.addBet(new Bet(BetType.FIELD, 10, 0));

        RollRecord record = engine.resolveRoll(game, new DiceRoll(3, 4)); // 7

        // Pass Line wins, Field loses on 7
        assertEquals(2, record.resolutions().size());
        long wins = record.resolutions().stream().filter(r -> r.outcome() == BetStatus.WON).count();
        long losses = record.resolutions().stream().filter(r -> r.outcome() == BetStatus.LOST).count();
        assertEquals(1, wins);
        assertEquals(1, losses);
    }

    @Test
    void rollRecordCapturesCorrectState() {
        Game game = newGame();
        game.addBet(new Bet(BetType.PASS_LINE, 25, 0));

        RollRecord record = engine.resolveRoll(game, new DiceRoll(2, 4));

        assertEquals(1, record.rollNumber());
        assertEquals(2, record.dice().die1());
        assertEquals(4, record.dice().die2());
        assertEquals(RoundPhase.COME_OUT, record.phaseBefore());
        assertNull(record.pointBefore());
        assertEquals(RoundPhase.POINT, record.phaseAfter());
        assertEquals(6, record.pointAfter());
        assertNotNull(record.timestamp());
    }
}
