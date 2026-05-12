package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;
import com.gitgames.craps.service.InvalidBetException;

public class BetValidator {

    public void validate(Game game, BetType type, int amount, Integer targetNumber) {
        if (game.getGamePhase() != GamePhase.PLAYING) {
            throw new InvalidBetException("Game is over");
        }

        TableSettings settings = game.getSettings();

        // Horn must be divisible by 4
        if (type == BetType.HORN && amount % 4 != 0) {
            throw new InvalidBetException("Horn bet must be divisible by 4");
        }

        if (!type.isOddsBet()) {
            if (amount < settings.minBet()) {
                throw new InvalidBetException("Minimum bet is " + settings.minBet());
            }
            if (amount > settings.maxBet()) {
                throw new InvalidBetException("Maximum bet is " + settings.maxBet());
            }
        }

        if (amount > game.getBankroll()) {
            throw new InvalidBetException("Insufficient bankroll");
        }

        switch (type) {
            case PASS_LINE -> {
                if (game.getRoundPhase() != RoundPhase.COME_OUT) {
                    throw new InvalidBetException("Pass Line bets can only be placed during the Come-Out roll");
                }
                if (game.hasActiveBetOfType(BetType.PASS_LINE)) {
                    throw new InvalidBetException("Already have a Pass Line bet");
                }
            }
            case DONT_PASS -> {
                if (game.getRoundPhase() != RoundPhase.COME_OUT) {
                    throw new InvalidBetException("Don't Pass bets can only be placed during the Come-Out roll");
                }
                if (game.hasActiveBetOfType(BetType.DONT_PASS)) {
                    throw new InvalidBetException("Already have a Don't Pass bet");
                }
            }
            case COME -> {
                if (game.getRoundPhase() != RoundPhase.POINT) {
                    throw new InvalidBetException("Come bets can only be placed during the Point phase");
                }
            }
            case DONT_COME -> {
                if (game.getRoundPhase() != RoundPhase.POINT) {
                    throw new InvalidBetException("Don't Come bets can only be placed during the Point phase");
                }
            }
            case PASS_ODDS -> {
                Bet passLine = game.findActiveBetByType(BetType.PASS_LINE);
                if (passLine == null || game.getRoundPhase() != RoundPhase.POINT) {
                    throw new InvalidBetException("Must have a Pass Line bet with a point established");
                }
                if (game.hasActiveBetOfType(BetType.PASS_ODDS)) {
                    throw new InvalidBetException("Already have Pass Line Odds");
                }
                validateOddsAmount(amount, passLine.getAmount(), game.getPoint(), settings);
            }
            case DONT_PASS_ODDS -> {
                Bet dontPass = game.findActiveBetByType(BetType.DONT_PASS);
                if (dontPass == null || game.getRoundPhase() != RoundPhase.POINT) {
                    throw new InvalidBetException("Must have a Don't Pass bet with a point established");
                }
                if (game.hasActiveBetOfType(BetType.DONT_PASS_ODDS)) {
                    throw new InvalidBetException("Already have Don't Pass Odds");
                }
                validateLayOddsAmount(amount, dontPass.getAmount(), game.getPoint(), settings);
            }
            case COME_ODDS -> {
                if (targetNumber == null) {
                    throw new InvalidBetException("Must specify which Come point for odds");
                }
                Bet comeBet = game.findActiveBetsByType(BetType.COME).stream()
                    .filter(b -> targetNumber.equals(b.getPointNumber()))
                    .findFirst()
                    .orElse(null);
                if (comeBet == null) {
                    throw new InvalidBetException("No Come bet on " + targetNumber);
                }
                boolean hasOdds = game.findActiveBetsByType(BetType.COME_ODDS).stream()
                    .anyMatch(b -> targetNumber.equals(b.getPointNumber()));
                if (hasOdds) {
                    throw new InvalidBetException("Already have Come Odds on " + targetNumber);
                }
                validateOddsAmount(amount, comeBet.getAmount(), targetNumber, settings);
            }
            case DONT_COME_ODDS -> {
                if (targetNumber == null) {
                    throw new InvalidBetException("Must specify which Don't Come point for odds");
                }
                Bet dontComeBet = game.findActiveBetsByType(BetType.DONT_COME).stream()
                    .filter(b -> targetNumber.equals(b.getPointNumber()))
                    .findFirst()
                    .orElse(null);
                if (dontComeBet == null) {
                    throw new InvalidBetException("No Don't Come bet on " + targetNumber);
                }
                boolean hasOdds = game.findActiveBetsByType(BetType.DONT_COME_ODDS).stream()
                    .anyMatch(b -> targetNumber.equals(b.getPointNumber()));
                if (hasOdds) {
                    throw new InvalidBetException("Already have Don't Come Odds on " + targetNumber);
                }
                validateLayOddsAmount(amount, dontComeBet.getAmount(), targetNumber, settings);
            }
            case PLACE_4, PLACE_5, PLACE_6, PLACE_8, PLACE_9, PLACE_10 -> {
                if (game.hasActiveBetOfType(type)) {
                    throw new InvalidBetException("Already have a " + type.getDisplayName() + " bet");
                }
            }
            case HARD_4, HARD_6, HARD_8, HARD_10 -> {
                if (game.hasActiveBetOfType(type)) {
                    throw new InvalidBetException("Already have a " + type.getDisplayName() + " bet");
                }
            }
            default -> {
                // Single-roll bets and others: no special phase restrictions
            }
        }
    }

    private void validateOddsAmount(int oddsAmount, int flatBet, Integer point, TableSettings settings) {
        if (point == null) {
            throw new InvalidBetException("No point established for odds bet");
        }
        int maxMultiple = settings.oddsMultipleForPoint(point);
        int maxOdds = flatBet * maxMultiple;
        if (oddsAmount > maxOdds) {
            throw new InvalidBetException("Max odds for this point is " + maxMultiple + "x (" + maxOdds + ")");
        }
    }

    private void validateLayOddsAmount(int oddsAmount, int flatBet, Integer point, TableSettings settings) {
        if (point == null) {
            throw new InvalidBetException("No point established for odds bet");
        }
        int maxMultiple = settings.oddsMultipleForPoint(point);
        // For Don't bets, max odds is based on what you can WIN equaling the take-odds max
        int maxWin = flatBet * maxMultiple;
        int maxLay = switch (point) {
            case 4, 10 -> maxWin * 2;     // lay 2 to win 1, so max lay = maxWin * 2
            case 5, 9 -> maxWin * 3 / 2;  // lay 3 to win 2
            case 6, 8 -> maxWin * 6 / 5;  // lay 6 to win 5
            default -> maxWin;
        };
        if (oddsAmount > maxLay) {
            throw new InvalidBetException("Max lay odds for this point is " + maxLay);
        }
    }
}
