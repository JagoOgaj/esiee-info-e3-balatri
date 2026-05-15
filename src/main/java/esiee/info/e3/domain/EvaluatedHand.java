package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.Combination;

public record EvaluatedHand(Combination combo, int chips, int multiplier, int level) {}
