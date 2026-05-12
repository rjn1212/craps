package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.DiceRoll;

public record DiceRollDto(int die1, int die2, int total, boolean hardway) {
    public static DiceRollDto from(DiceRoll roll) {
        return new DiceRollDto(roll.die1(), roll.die2(), roll.total(), roll.isHardway());
    }
}
