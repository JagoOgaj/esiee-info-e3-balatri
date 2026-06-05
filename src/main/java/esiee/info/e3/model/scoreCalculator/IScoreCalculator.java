package esiee.info.e3.model.scoreCalculator;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.gameState.IGameState;
import java.util.List;

public sealed interface IScoreCalculator permits  ScoreCalculator{
    long calculateScore(Combination combo, List<Card> cards, int level, BlindConstraint constraint, IGameState state, List<JokerType> activeJokers, boolean isPreview);
    int computeBaseChips(Combination combo, int level);
    int computeMultiplier(Combination combo, int level);
}