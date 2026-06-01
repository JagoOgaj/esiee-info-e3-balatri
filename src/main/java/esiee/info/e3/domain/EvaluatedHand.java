package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.Combination;

import java.util.Objects;

public record EvaluatedHand(Combination combo, int chips, int multiplier, int level) {
    public EvaluatedHand {
        Objects.requireNonNull(combo);
        if (chips < 0) {
            throw new IllegalArgumentException();
        }
        if (multiplier < 0) {
            throw new IllegalArgumentException();
        }
        if (level < 0) {
            throw new IllegalArgumentException();
        }
    }
}
