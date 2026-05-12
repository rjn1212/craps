package com.gitgames.craps.engine;

import com.gitgames.craps.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BetResolverTest {

    private BetResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new BetResolver();
    }

    private Bet makeBet(BetType type, int amount) {
        return new Bet(type, amount, 0);
    }

    private Bet makeBetWithPoint(BetType type, int amount, int point) {
        Bet bet = new Bet(type, amount, 0);
        bet.setPointNumber(point);
        return bet;
    }

    @Nested
    class PassLine {

        @Test
        void winsOnNatural7ComeOut() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(1, res.size());
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(50, res.get(0).payout());
        }

        @Test
        void winsOnNatural11ComeOut() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOnCraps2() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
            assertEquals(0, res.get(0).payout());
        }

        @Test
        void losesOnCraps3() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void losesOnCraps12() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void noResolutionOnPointNumber() {
            Bet bet = makeBet(BetType.PASS_LINE, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 2), RoundPhase.COME_OUT, null);
            assertTrue(res.isEmpty());
        }

        @Test
        void winsOnPointDuringPointPhase() {
            Bet bet = makeBetWithPoint(BetType.PASS_LINE, 25, 6);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 2), RoundPhase.POINT, 6);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOnSevenDuringPointPhase() {
            Bet bet = makeBetWithPoint(BetType.PASS_LINE, 25, 6);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 6);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void noResolutionOnOtherNumberDuringPoint() {
            Bet bet = makeBetWithPoint(BetType.PASS_LINE, 25, 6);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 4), RoundPhase.POINT, 6);
            assertTrue(res.isEmpty());
        }
    }

    @Nested
    class DontPass {

        @Test
        void winsOn2ComeOut() {
            Bet bet = makeBet(BetType.DONT_PASS, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void winsOn3ComeOut() {
            Bet bet = makeBet(BetType.DONT_PASS, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void pushesOn12ComeOut() {
            Bet bet = makeBet(BetType.DONT_PASS, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.RETURNED, res.get(0).outcome());
            assertEquals(25, res.get(0).payout());
        }

        @Test
        void losesOn7ComeOut() {
            Bet bet = makeBet(BetType.DONT_PASS, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void losesOn11ComeOut() {
            Bet bet = makeBet(BetType.DONT_PASS, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void winsOn7DuringPoint() {
            Bet bet = makeBetWithPoint(BetType.DONT_PASS, 25, 6);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 6);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOnPointDuringPoint() {
            Bet bet = makeBetWithPoint(BetType.DONT_PASS, 25, 6);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 2), RoundPhase.POINT, 6);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }
    }

    @Nested
    class Come {

        @Test
        void winsOn7PersonalComeOut() {
            Bet bet = makeBet(BetType.COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void winsOn11PersonalComeOut() {
            Bet bet = makeBet(BetType.COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOn2PersonalComeOut() {
            Bet bet = makeBet(BetType.COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void noResolutionOnPointEstablishment() {
            Bet bet = makeBet(BetType.COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 3), RoundPhase.POINT, 8);
            assertTrue(res.isEmpty());
        }

        @Test
        void winsOnPersonalPoint() {
            Bet bet = makeBetWithPoint(BetType.COME, 25, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 3), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOn7WithPersonalPoint() {
            Bet bet = makeBetWithPoint(BetType.COME, 25, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }
    }

    @Nested
    class DontCome {

        @Test
        void winsOn2PersonalComeOut() {
            Bet bet = makeBet(BetType.DONT_COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void pushesOn12PersonalComeOut() {
            Bet bet = makeBet(BetType.DONT_COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.POINT, 8);
            assertEquals(BetStatus.RETURNED, res.get(0).outcome());
        }

        @Test
        void losesOn7PersonalComeOut() {
            Bet bet = makeBet(BetType.DONT_COME, 25);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void winsOn7WithPersonalPoint() {
            Bet bet = makeBetWithPoint(BetType.DONT_COME, 25, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOnPersonalPoint() {
            Bet bet = makeBetWithPoint(BetType.DONT_COME, 25, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 3), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }
    }

    @Nested
    class Odds {

        @Test
        void passOddsWinOnPoint() {
            Bet bet = makeBet(BetType.PASS_ODDS, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 2), RoundPhase.POINT, 4);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            // 30 + 30*2 = 90
            assertEquals(90, res.get(0).payout());
        }

        @Test
        void passOddsLoseOn7() {
            Bet bet = makeBet(BetType.PASS_ODDS, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 4);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void passOddsNoResolutionOnOther() {
            Bet bet = makeBet(BetType.PASS_ODDS, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 4), RoundPhase.POINT, 4);
            assertTrue(res.isEmpty());
        }

        @Test
        void dontPassOddsWinOn7() {
            Bet bet = makeBet(BetType.DONT_PASS_ODDS, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 4);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            // 30 + 30/2 = 45
            assertEquals(45, res.get(0).payout());
        }

        @Test
        void comeOddsWinOnPersonalPoint() {
            Bet bet = makeBetWithPoint(BetType.COME_ODDS, 20, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 3), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            // 20 + 20*3/2 = 50
            assertEquals(50, res.get(0).payout());
        }

        @Test
        void dontComeOddsWinOn7() {
            Bet bet = makeBetWithPoint(BetType.DONT_COME_ODDS, 20, 5);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }
    }

    @Nested
    class PlaceBets {

        @Test
        void placeWinsOnTarget() {
            Bet bet = makeBet(BetType.PLACE_6, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 2), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(65, res.get(0).payout());
        }

        @Test
        void placeLosesOn7() {
            Bet bet = makeBet(BetType.PLACE_6, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void placeNoResolutionOnOther() {
            Bet bet = makeBet(BetType.PLACE_6, 30);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 4), RoundPhase.POINT, 8);
            assertTrue(res.isEmpty());
        }

        @Test
        void placeSkippedWhenOff() {
            Bet bet = makeBet(BetType.PLACE_6, 30);
            bet.setOn(false);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 2), RoundPhase.POINT, 8);
            assertTrue(res.isEmpty());
            assertTrue(bet.isActive());
        }
    }

    @Nested
    class Field {

        @Test
        void winsOn3() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(20, res.get(0).payout());
        }

        @Test
        void winsOn4() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 3), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void winsOn9() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 5), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void losesOn5() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 3), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void losesOn6() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(2, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void losesOn7() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void losesOn8() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 5), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void doublesOn2() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(30, res.get(0).payout());
        }

        @Test
        void triplesOn12() {
            Bet bet = makeBet(BetType.FIELD, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(40, res.get(0).payout());
        }
    }

    @Nested
    class Propositions {

        @Test
        void anySeven() {
            Bet bet = makeBet(BetType.ANY_SEVEN, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void anySevenLoses() {
            Bet bet = makeBet(BetType.ANY_SEVEN, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 3), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void anyCraps2() {
            Bet bet = makeBet(BetType.ANY_CRAPS, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void anyCraps3() {
            Bet bet = makeBet(BetType.ANY_CRAPS, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void anyCraps12() {
            Bet bet = makeBet(BetType.ANY_CRAPS, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void yo() {
            Bet bet = makeBet(BetType.YO, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void aces() {
            Bet bet = makeBet(BetType.ACES, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void acesLoses() {
            Bet bet = makeBet(BetType.ACES, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void boxcars() {
            Bet bet = makeBet(BetType.BOXCARS, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }
    }

    @Nested
    class Hardways {

        @Test
        void hard6WinsOnHardway() {
            Bet bet = makeBet(BetType.HARD_6, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 3), RoundPhase.POINT, 8);
            assertEquals(BetStatus.WON, res.get(0).outcome());
        }

        @Test
        void hard6LosesOnEasyWay() {
            Bet bet = makeBet(BetType.HARD_6, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 2), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void hard6LosesOn7() {
            Bet bet = makeBet(BetType.HARD_6, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.POINT, 8);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }

        @Test
        void hard6StaysActiveOnOther() {
            Bet bet = makeBet(BetType.HARD_6, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(4, 4), RoundPhase.POINT, 8);
            assertTrue(res.isEmpty());
            assertTrue(bet.isActive());
        }
    }

    @Nested
    class CAndE {

        @Test
        void winsOnCraps() {
            Bet bet = makeBet(BetType.C_AND_E, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(40, res.get(0).payout());
        }

        @Test
        void winsOnYo() {
            Bet bet = makeBet(BetType.C_AND_E, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(80, res.get(0).payout());
        }

        @Test
        void losesOnOther() {
            Bet bet = makeBet(BetType.C_AND_E, 10);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }
    }

    @Nested
    class Horn {

        @Test
        void winsOn2() {
            Bet bet = makeBet(BetType.HORN, 4);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 1), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(28, res.get(0).payout());
        }

        @Test
        void winsOn3() {
            Bet bet = makeBet(BetType.HORN, 4);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(1, 2), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(13, res.get(0).payout());
        }

        @Test
        void winsOn11() {
            Bet bet = makeBet(BetType.HORN, 4);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(5, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(13, res.get(0).payout());
        }

        @Test
        void winsOn12() {
            Bet bet = makeBet(BetType.HORN, 4);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(6, 6), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.WON, res.get(0).outcome());
            assertEquals(28, res.get(0).payout());
        }

        @Test
        void losesOnOther() {
            Bet bet = makeBet(BetType.HORN, 4);
            List<BetResolution> res = resolver.resolveAllBets(List.of(bet), new DiceRoll(3, 4), RoundPhase.COME_OUT, null);
            assertEquals(BetStatus.LOST, res.get(0).outcome());
        }
    }
}
