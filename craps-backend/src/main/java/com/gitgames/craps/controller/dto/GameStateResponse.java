package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Game;

import java.util.List;

public record GameStateResponse(
    String gameId,
    int bankroll,
    int startingBankroll,
    String roundPhase,
    Integer point,
    String gamePhase,
    List<BetDto> activeBets,
    List<RollRecordDto> recentRolls,
    int rollCount,
    int totalBetsOnTable,
    String gameOverReason,
    int minBet,
    int maxBet,
    int maxOddsMultiple
) {
    public static GameStateResponse from(Game game) {
        List<RollRecordDto> recentRolls = game.getRollHistory().stream()
            .skip(Math.max(0, game.getRollHistory().size() - 10))
            .map(RollRecordDto::from)
            .toList();

        return new GameStateResponse(
            game.getId(),
            game.getBankroll(),
            game.getStartingBankroll(),
            game.getRoundPhase().name(),
            game.getPoint(),
            game.getGamePhase().name(),
            game.getActiveBets().stream().map(BetDto::from).toList(),
            recentRolls,
            game.getRollCount(),
            game.getTotalBetsOnTable(),
            game.getGameOverReason(),
            game.getSettings().minBet(),
            game.getSettings().maxBet(),
            game.getSettings().maxOddsMultiple()
        );
    }
}
