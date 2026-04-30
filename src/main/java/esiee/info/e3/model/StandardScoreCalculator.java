package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.Combination;

import java.util.List;

public class StandardScoreCalculator implements IScoreCalculator{
    @Override
    public long calculateScore(Combination combo, List<Card> cards, int level) {
        var chips = combo.getBasicChips() + (level - 1) * 10;
        var mult = combo.getMultiplier() + (level - 1) * 2;

        for (Card c : cards) {
            chips += c.rank().getValue();
        }

        return (long) chips * mult;
    }
}
