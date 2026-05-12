package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Game;

import java.util.List;

public record RemoveBetResponse(
    int bankroll,
    List<BetDto> activeBets
) {
    public static RemoveBetResponse from(Game game) {
        return new RemoveBetResponse(
            game.getBankroll(),
            game.getActiveBets().stream().map(BetDto::from).toList()
        );
    }
}
