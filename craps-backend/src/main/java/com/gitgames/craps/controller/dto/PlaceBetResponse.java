package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Bet;
import com.gitgames.craps.model.Game;

import java.util.List;

public record PlaceBetResponse(
    BetDto placedBet,
    int bankroll,
    List<BetDto> activeBets
) {
    public static PlaceBetResponse from(Bet bet, Game game) {
        return new PlaceBetResponse(
            BetDto.from(bet),
            game.getBankroll(),
            game.getActiveBets().stream().map(BetDto::from).toList()
        );
    }
}
