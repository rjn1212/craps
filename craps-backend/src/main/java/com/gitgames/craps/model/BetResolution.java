package com.gitgames.craps.model;

public record BetResolution(
    String betId,
    BetType betType,
    int wagerAmount,
    int payout,
    BetStatus outcome
) {}
