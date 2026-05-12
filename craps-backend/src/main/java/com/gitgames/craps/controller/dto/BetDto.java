package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Bet;

public record BetDto(
    String id,
    String betType,
    String displayName,
    int amount,
    String status,
    Integer pointNumber,
    boolean isOn
) {
    public static BetDto from(Bet bet) {
        return new BetDto(
            bet.getId(),
            bet.getType().name(),
            bet.getType().getDisplayName(),
            bet.getAmount(),
            bet.getStatus().name(),
            bet.getPointNumber(),
            bet.isOn()
        );
    }
}
