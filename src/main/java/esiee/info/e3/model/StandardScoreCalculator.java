package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.model.interfaces.IScoreCalculator;
import java.util.List;

public class StandardScoreCalculator implements IScoreCalculator {

    @Override
    public long calculateScore(Combination combo, List<Card> cards, int level) {
        var chips = this.computeBaseChips(combo, level);
        var multiplier = this.computeMultiplier(combo, level);

        for (var c : cards) {
            chips += c.rank().getValue();
        }

        return (long) chips * multiplier;
    }

    @Override
    public int computeBaseChips(Combination combo, int level) {
        var planet = Planet.fromCombination(combo);
        var bonus = (planet != null) ? planet.getChipsBonus() : 10;
        return combo.getBasicChips() + (level - 1) * bonus;
    }

    @Override
    public int computeMultiplier(Combination combo, int level) {
        var planet = Planet.fromCombination(combo);
        var bonus = (planet != null) ? planet.getMultiplierBonus() : 2;
        return combo.getMultiplier() + (level - 1) * bonus;
    }
}