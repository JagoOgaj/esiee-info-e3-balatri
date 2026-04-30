package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.Combination;

import java.util.List;

public interface IScoreCalculator {
    long calculateScore(Combination combo, List<Card> cards, int level);
}
