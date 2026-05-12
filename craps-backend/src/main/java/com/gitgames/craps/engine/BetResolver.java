package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;

import java.util.ArrayList;
import java.util.List;

public class BetResolver {

    public List<BetResolution> resolveAllBets(List<Bet> activeBets, DiceRoll roll,
                                               RoundPhase phase, Integer gamePoint) {
        List<BetResolution> resolutions = new ArrayList<>();

        for (Bet bet : activeBets) {
            if (!bet.isOn()) continue;

            BetResolution resolution = resolveBet(bet, roll, phase, gamePoint);
            if (resolution != null) {
                resolutions.add(resolution);
                bet.setStatus(resolution.outcome());
            }
        }

        return resolutions;
    }

    private BetResolution resolveBet(Bet bet, DiceRoll roll, RoundPhase phase, Integer gamePoint) {
        return switch (bet.getType()) {
            case PASS_LINE -> resolvePassLine(bet, roll, phase);
            case DONT_PASS -> resolveDontPass(bet, roll, phase);
            case PASS_ODDS -> resolvePassOdds(bet, roll, phase, gamePoint);
            case DONT_PASS_ODDS -> resolveDontPassOdds(bet, roll, phase, gamePoint);
            case COME -> resolveCome(bet, roll);
            case DONT_COME -> resolveDontCome(bet, roll);
            case COME_ODDS -> resolveComeOdds(bet, roll);
            case DONT_COME_ODDS -> resolveDontComeOdds(bet, roll);
            case PLACE_4, PLACE_5, PLACE_6, PLACE_8, PLACE_9, PLACE_10 -> resolvePlace(bet, roll);
            case FIELD -> resolveField(bet, roll);
            case ANY_SEVEN -> resolveSingleRoll(bet, roll, 7);
            case ANY_CRAPS -> resolveAnyCraps(bet, roll);
            case YO -> resolveSingleRoll(bet, roll, 11);
            case ACES -> resolveSingleRoll(bet, roll, 2);
            case BOXCARS -> resolveSingleRoll(bet, roll, 12);
            case HARD_4, HARD_6, HARD_8, HARD_10 -> resolveHardway(bet, roll);
            case C_AND_E -> resolveCAndE(bet, roll);
            case HORN -> resolveHorn(bet, roll);
        };
    }

    private BetResolution resolvePassLine(Bet bet, DiceRoll roll, RoundPhase phase) {
        int total = roll.total();
        if (phase == RoundPhase.COME_OUT) {
            if (total == 7 || total == 11) return won(bet, roll);
            if (total == 2 || total == 3 || total == 12) return lost(bet);
            return null; // point established — no resolution
        } else {
            if (total == bet.getPointNumber()) return won(bet, roll);
            if (total == 7) return lost(bet);
            return null;
        }
    }

    private BetResolution resolveDontPass(Bet bet, DiceRoll roll, RoundPhase phase) {
        int total = roll.total();
        if (phase == RoundPhase.COME_OUT) {
            if (total == 2 || total == 3) return won(bet, roll);
            if (total == 12) return returned(bet);
            if (total == 7 || total == 11) return lost(bet);
            return null;
        } else {
            if (total == 7) return won(bet, roll);
            if (total == bet.getPointNumber()) return lost(bet);
            return null;
        }
    }

    private BetResolution resolvePassOdds(Bet bet, DiceRoll roll, RoundPhase phase, Integer gamePoint) {
        if (phase != RoundPhase.POINT || gamePoint == null) return null;
        int total = roll.total();
        if (total == gamePoint) return won(bet, roll, gamePoint);
        if (total == 7) return lost(bet);
        return null;
    }

    private BetResolution resolveDontPassOdds(Bet bet, DiceRoll roll, RoundPhase phase, Integer gamePoint) {
        if (phase != RoundPhase.POINT || gamePoint == null) return null;
        int total = roll.total();
        if (total == 7) return won(bet, roll, gamePoint);
        if (total == gamePoint) return lost(bet);
        return null;
    }

    private BetResolution resolveCome(Bet bet, DiceRoll roll) {
        int total = roll.total();
        if (bet.getPointNumber() == null) {
            // Personal come-out roll
            if (total == 7 || total == 11) return won(bet, roll);
            if (total == 2 || total == 3 || total == 12) return lost(bet);
            // Establish personal point — handled by CrapsEngine after resolution
            return null;
        } else {
            // Has a personal point
            if (total == bet.getPointNumber()) return won(bet, roll);
            if (total == 7) return lost(bet);
            return null;
        }
    }

    private BetResolution resolveDontCome(Bet bet, DiceRoll roll) {
        int total = roll.total();
        if (bet.getPointNumber() == null) {
            if (total == 2 || total == 3) return won(bet, roll);
            if (total == 12) return returned(bet);
            if (total == 7 || total == 11) return lost(bet);
            return null;
        } else {
            if (total == 7) return won(bet, roll);
            if (total == bet.getPointNumber()) return lost(bet);
            return null;
        }
    }

    private BetResolution resolveComeOdds(Bet bet, DiceRoll roll) {
        if (bet.getPointNumber() == null) return null;
        int total = roll.total();
        if (total == bet.getPointNumber()) return won(bet, roll, bet.getPointNumber());
        if (total == 7) return lost(bet);
        return null;
    }

    private BetResolution resolveDontComeOdds(Bet bet, DiceRoll roll) {
        if (bet.getPointNumber() == null) return null;
        int total = roll.total();
        if (total == 7) return won(bet, roll, bet.getPointNumber());
        if (total == bet.getPointNumber()) return lost(bet);
        return null;
    }

    private BetResolution resolvePlace(Bet bet, DiceRoll roll) {
        int target = bet.getType().placeNumber();
        int total = roll.total();
        if (total == target) return won(bet, roll);
        if (total == 7) return lost(bet);
        return null;
    }

    private BetResolution resolveField(Bet bet, DiceRoll roll) {
        int total = roll.total();
        if (total == 2 || total == 3 || total == 4 || total == 9 || total == 10 || total == 11 || total == 12) {
            return won(bet, roll);
        }
        return lost(bet);
    }

    private BetResolution resolveSingleRoll(Bet bet, DiceRoll roll, int targetTotal) {
        if (roll.total() == targetTotal) return won(bet, roll);
        return lost(bet);
    }

    private BetResolution resolveAnyCraps(Bet bet, DiceRoll roll) {
        if (roll.isCraps()) return won(bet, roll);
        return lost(bet);
    }

    private BetResolution resolveHardway(Bet bet, DiceRoll roll) {
        int target = bet.getType().hardwayNumber();
        int total = roll.total();
        if (total == target && roll.isHardway()) return won(bet, roll);
        if (total == target || total == 7) return lost(bet);
        return null; // stays active
    }

    private BetResolution resolveCAndE(Bet bet, DiceRoll roll) {
        int total = roll.total();
        if (roll.isCraps() || total == 11) return won(bet, roll);
        return lost(bet);
    }

    private BetResolution resolveHorn(Bet bet, DiceRoll roll) {
        int total = roll.total();
        if (total == 2 || total == 3 || total == 11 || total == 12) return won(bet, roll);
        return lost(bet);
    }

    private BetResolution won(Bet bet, DiceRoll roll) {
        return won(bet, roll, null);
    }

    private BetResolution won(Bet bet, DiceRoll roll, Integer pointNumber) {
        int payout = PayoutCalculator.calculateWinPayout(bet.getType(), bet.getAmount(), pointNumber, roll);
        return new BetResolution(bet.getId(), bet.getType(), bet.getAmount(), payout, BetStatus.WON);
    }

    private BetResolution lost(Bet bet) {
        return new BetResolution(bet.getId(), bet.getType(), bet.getAmount(), 0, BetStatus.LOST);
    }

    private BetResolution returned(Bet bet) {
        return new BetResolution(bet.getId(), bet.getType(), bet.getAmount(), bet.getAmount(), BetStatus.RETURNED);
    }
}
