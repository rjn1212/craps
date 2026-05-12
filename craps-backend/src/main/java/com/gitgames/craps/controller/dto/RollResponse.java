package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.Game;
import com.gitgames.craps.model.RollRecord;

import java.util.List;

public record RollResponse(
    DiceRollDto dice,
    String roundPhaseBefore,
    Integer pointBefore,
    String roundPhaseAfter,
    Integer pointAfter,
    List<BetResolutionDto> resolutions,
    int bankroll,
    List<BetDto> activeBets,
    String gamePhase,
    String gameOverReason,
    int rollCount
) {
    public static RollResponse from(Game game, RollRecord record) {
        return new RollResponse(
            DiceRollDto.from(record.dice()),
            record.phaseBefore().name(),
            record.pointBefore(),
            record.phaseAfter().name(),
            record.pointAfter(),
            record.resolutions().stream().map(BetResolutionDto::from).toList(),
            game.getBankroll(),
            game.getActiveBets().stream().map(BetDto::from).toList(),
            game.getGamePhase().name(),
            game.getGameOverReason(),
            game.getRollCount()
        );
    }
}
