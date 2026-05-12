package com.gitgames.craps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gitgames.craps.controller.dto.*;
import com.gitgames.craps.engine.BetValidator;
import com.gitgames.craps.engine.CrapsEngine;
import com.gitgames.craps.model.*;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlatformScoreReporter scoreReporter;
    private final ObjectMapper objectMapper;
    private final CrapsEngine crapsEngine = new CrapsEngine();
    private final BetValidator betValidator = new BetValidator();

    public GameService(GameRepository gameRepository,
                       PlatformScoreReporter scoreReporter,
                       ObjectMapper objectMapper) {
        this.gameRepository = gameRepository;
        this.scoreReporter = scoreReporter;
        this.objectMapper = objectMapper;
    }

    public GameStateResponse createGame(CreateGameRequest request, String platformUserId) {
        int bankroll = request.startingBankroll() > 0 ? request.startingBankroll() : 1000;
        int oddsMultiple = request.maxOddsMultiple() > 0 ? request.maxOddsMultiple() : TableSettings.ODDS_3_4_5X;
        TableSettings settings = new TableSettings(5, 500, oddsMultiple);

        Game game = new Game(bankroll, settings);
        game.setPlatformUserId(platformUserId);
        gameRepository.save(game);
        return GameStateResponse.from(game);
    }

    public GameStateResponse getGame(String gameId) {
        return GameStateResponse.from(gameRepository.findById(gameId));
    }

    public PlaceBetResponse placeBet(String gameId, PlaceBetRequest request) {
        Game game = gameRepository.findById(gameId);
        betValidator.validate(game, request.betType(), request.amount(), request.targetNumber());

        Bet bet = new Bet(request.betType(), request.amount(), game.getRollCount());

        // For Come/Don't Come odds, set the point number so resolution knows which come point
        if (request.targetNumber() != null &&
            (request.betType() == BetType.COME_ODDS || request.betType() == BetType.DONT_COME_ODDS)) {
            bet.setPointNumber(request.targetNumber());
        }

        // Place bets default to OFF during come-out (casino convention)
        if (request.betType().isPlaceBet() && game.getRoundPhase() == RoundPhase.COME_OUT) {
            bet.setOn(false);
        }

        game.addBet(bet);
        return PlaceBetResponse.from(bet, game);
    }

    public RemoveBetResponse removeBet(String gameId, String betId) {
        Game game = gameRepository.findById(gameId);
        Bet bet = game.findBetById(betId);
        if (bet == null || !bet.isActive()) {
            throw new InvalidBetException("Bet not found or already resolved");
        }
        if (bet.getType().isContractBet() && game.getRoundPhase() == RoundPhase.POINT) {
            // Contract bets can't be removed once point is established
            // Exception: Don't Pass/Don't Come CAN be taken down (but it's disadvantageous)
            if (bet.getType() == BetType.PASS_LINE || bet.getType() == BetType.COME) {
                throw new InvalidBetException("Cannot remove a " + bet.getType().getDisplayName() + " bet after point is established");
            }
        }
        game.removeBet(betId);
        return RemoveBetResponse.from(game);
    }

    public GameStateResponse toggleBet(String gameId, String betId) {
        Game game = gameRepository.findById(gameId);
        Bet bet = game.findBetById(betId);
        if (bet == null || !bet.isActive()) {
            throw new InvalidBetException("Bet not found or already resolved");
        }
        if (!bet.getType().isPlaceBet() && !bet.getType().isHardway()) {
            throw new InvalidBetException("Only Place and Hardway bets can be toggled on/off");
        }
        bet.setOn(!bet.isOn());
        return GameStateResponse.from(game);
    }

    public RollResponse roll(String gameId) {
        Game game = gameRepository.findById(gameId);
        if (game.getGamePhase() != GamePhase.PLAYING) {
            throw new InvalidActionException("Game is over");
        }
        if (game.getActiveBets().isEmpty()) {
            throw new InvalidActionException("Must place at least one bet before rolling");
        }

        DiceRoll dice = crapsEngine.rollDice();
        RollRecord record = crapsEngine.resolveRoll(game, dice);

        if (game.getGamePhase() == GamePhase.GAME_OVER) {
            reportScore(game);
        }

        return RollResponse.from(game, record);
    }

    public CashOutResponse cashOut(String gameId) {
        Game game = gameRepository.findById(gameId);
        if (game.getGamePhase() != GamePhase.PLAYING) {
            throw new InvalidActionException("Game is already over");
        }

        // Return all active bets to bankroll
        for (Bet bet : game.getActiveBets()) {
            game.creditBankroll(bet.getAmount());
            bet.setStatus(BetStatus.RETURNED);
        }
        game.clearResolvedBets();

        game.setGamePhase(GamePhase.GAME_OVER);
        game.setGameOverReason("cashed_out");
        reportScore(game);

        return CashOutResponse.from(game);
    }

    public void abandonGame(String gameId) {
        gameRepository.delete(gameId);
    }

    private void reportScore(Game game) {
        int netResult = game.getBankroll() - game.getStartingBankroll();
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("result", netResult >= 0 ? "WIN" : "LOSS");
        metadata.put("netResult", netResult);
        metadata.put("finalBankroll", game.getBankroll());
        metadata.put("startingBankroll", game.getStartingBankroll());
        metadata.put("totalRolls", game.getRollCount());
        metadata.put("totalBetsPlaced", game.getTotalBetsPlaced());
        metadata.put("gameOverReason", game.getGameOverReason());

        scoreReporter.reportGameResult(
            game.getPlatformUserId(),
            game.getBankroll(),
            metadata
        );
    }
}
