package com.gitgames.craps.model;

import java.time.Instant;
import java.util.List;

public record RollRecord(
    int rollNumber,
    DiceRoll dice,
    RoundPhase phaseBefore,
    Integer pointBefore,
    RoundPhase phaseAfter,
    Integer pointAfter,
    List<BetResolution> resolutions,
    int bankrollAfter,
    Instant timestamp
) {}
