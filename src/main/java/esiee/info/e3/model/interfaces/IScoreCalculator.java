package esiee.info.e3.model.interfaces;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.GameState;
import java.util.List;

public interface IScoreCalculator {
    long calculateScore(Combination combo, List<Card> cards, int level, BlindConstraint constraint, GameState state, List<JokerType> activeJokers);
    int computeBaseChips(Combination combo, int level);
    int computeMultiplier(Combination combo, int level);
}