package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CrapsEngine {

    private final BetResolver betResolver = new BetResolver();

    public DiceRoll rollDice() {
        int die1 = ThreadLocalRandom.current().nextInt(1, 7);
        int die2 = ThreadLocalRandom.current().nextInt(1, 7);
        return new DiceRoll(die1, die2);
    }

    public RollRecord resolveRoll(Game game, DiceRoll roll) {
        RoundPhase phaseBefore = game.getRoundPhase();
        Integer pointBefore = game.getPoint();

        // Resolve all active bets
        List<BetResolution> resolutions = betResolver.resolveAllBets(
            game.getActiveBets(), roll, phaseBefore, game.getPoint());

        // Credit winnings and returns to bankroll
        for (BetResolution res : resolutions) {
            if (res.payout() > 0) {
                game.creditBankroll(res.payout());
            }
        }

        // Update Come/Don't Come bets that need to establish personal points
        establishComePoints(game, roll, phaseBefore);

        // Phase transitions
        applyPhaseTransition(game, roll, phaseBefore);

        // Toggle Place bets OFF when transitioning back to COME_OUT
        if (phaseBefore == RoundPhase.POINT && game.getRoundPhase() == RoundPhase.COME_OUT) {
            for (Bet bet : game.getActiveBets()) {
                if (bet.getType().isPlaceBet()) {
                    bet.setOn(false);
                }
            }
        }

        // Clean up resolved bets
        game.clearResolvedBets();

        // Check game over
        if (game.getBankroll() == 0 && game.getActiveBets().isEmpty()) {
            game.setGamePhase(GamePhase.GAME_OVER);
            game.setGameOverReason("broke");
        }

        RollRecord record = new RollRecord(
            game.getRollCount() + 1,
            roll,
            phaseBefore,
            pointBefore,
            game.getRoundPhase(),
            game.getPoint(),
            resolutions,
            game.getBankroll(),
            Instant.now()
        );

        game.addRollRecord(record);
        return record;
    }

    private void applyPhaseTransition(Game game, DiceRoll roll, RoundPhase phaseBefore) {
        int total = roll.total();

        if (phaseBefore == RoundPhase.COME_OUT) {
            if (roll.isPointNumber()) {
                game.setRoundPhase(RoundPhase.POINT);
                game.setPoint(total);
                // Set pointNumber on Pass Line and Don't Pass bets
                for (Bet bet : game.getActiveBets()) {
                    if ((bet.getType() == BetType.PASS_LINE || bet.getType() == BetType.DONT_PASS)
                        && bet.getPointNumber() == null) {
                        bet.setPointNumber(total);
                    }
                }
            }
            // 7, 11, 2, 3, 12 → stay in COME_OUT
        } else {
            // POINT phase
            if (total == game.getPoint() || total == 7) {
                game.setRoundPhase(RoundPhase.COME_OUT);
                game.setPoint(null);
            }
        }
    }

    private void establishComePoints(Game game, DiceRoll roll, RoundPhase phaseBefore) {
        int total = roll.total();
        if (!isPointNumber(total)) return;

        for (Bet bet : game.getActiveBets()) {
            if (bet.getPointNumber() != null) continue;

            if (bet.getType() == BetType.COME || bet.getType() == BetType.DONT_COME) {
                bet.setPointNumber(total);
            }
        }
    }

    private boolean isPointNumber(int total) {
        return total == 4 || total == 5 || total == 6 || total == 8 || total == 9 || total == 10;
    }
}
