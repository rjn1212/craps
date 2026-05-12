package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.BetResolution;

public record BetResolutionDto(
    String betId,
    String betType,
    String displayName,
    int wagerAmount,
    int payout,
    String outcome
) {
    public static BetResolutionDto from(BetResolution r) {
        return new BetResolutionDto(
            r.betId(),
            r.betType().name(),
            r.betType().getDisplayName(),
            r.wagerAmount(),
            r.payout(),
            r.outcome().name()
        );
    }
}
