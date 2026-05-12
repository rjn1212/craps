package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.BetType;

public record PlaceBetRequest(BetType betType, int amount, Integer targetNumber) {}
