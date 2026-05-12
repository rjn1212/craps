package com.gitgames.craps.controller.dto;

import com.gitgames.craps.model.RollRecord;

import java.util.List;

public record RollRecordDto(
    int rollNumber,
    DiceRollDto dice,
    String phaseBefore,
    Integer pointBefore,
    String phaseAfter,
    Integer pointAfter,
    List<BetResolutionDto> resolutions,
    int bankrollAfter
) {
    public static RollRecordDto from(RollRecord r) {
        return new RollRecordDto(
            r.rollNumber(),
            DiceRollDto.from(r.dice()),
            r.phaseBefore().name(),
            r.pointBefore(),
            r.phaseAfter().name(),
            r.pointAfter(),
            r.resolutions().stream().map(BetResolutionDto::from).toList(),
            r.bankrollAfter()
        );
    }
}
