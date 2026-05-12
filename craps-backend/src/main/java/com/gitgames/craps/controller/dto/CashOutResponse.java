package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Game;

public record CashOutResponse(
    int finalBankroll,
    int netResult,
    int totalRolls,
    int totalBetsPlaced
) {
    public static CashOutResponse from(Game game) {
        return new CashOutResponse(
            game.getBankroll(),
            game.getBankroll() - game.getStartingBankroll(),
            game.getRollCount(),
            game.getTotalBetsPlaced()
        );
    }
}
