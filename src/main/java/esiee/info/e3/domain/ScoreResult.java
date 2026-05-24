package esiee.info.e3.domain;

public record ScoreResult(long chips, long multiplier) {
    public ScoreResult withChips(long newChips) {
        return new ScoreResult(newChips, this.multiplier);
    }

    public ScoreResult withMultiplier(long newMultiplier) {
        return new ScoreResult(this.chips, newMultiplier);
    }
}