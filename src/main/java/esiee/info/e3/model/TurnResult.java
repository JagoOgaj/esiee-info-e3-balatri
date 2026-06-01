package esiee.info.e3.model;

public sealed interface TurnResult
    permits TurnResultBlindBeaten,
        TurnResultError,
        TurnResultFailure,
        TurnResultGameLost,
        TurnResultGameWon,
        TurnResultHandPlayed {}
