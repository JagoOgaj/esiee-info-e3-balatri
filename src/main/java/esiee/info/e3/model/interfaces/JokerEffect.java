package esiee.info.e3.model.interfaces;

import esiee.info.e3.domain.JokerContext;
import esiee.info.e3.domain.ScoreResult;

@FunctionalInterface
public interface JokerEffect {
    ScoreResult apply(JokerContext ctx, ScoreResult currentScore);
}