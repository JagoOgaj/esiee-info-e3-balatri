package esiee.info.e3.model.interfaces;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;
import java.util.List;

public interface IScoreCalculator {
  long calculateScore(Combination combo, List<Card> cards, int level);

  int computeBaseChips(Combination combo, int level);

  int computeMultiplier(Combination combo, int level);
}
