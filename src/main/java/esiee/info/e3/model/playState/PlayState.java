package esiee.info.e3.model.playState;

public sealed interface PlayState
    permits PlayStateBlindBeaten,
        PlayStateError,
        PlayStateFailure,
        PlayStateGameLost,
        PlayStateGameWon,
        PlayStateHandPlayed {}
