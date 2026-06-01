package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.model.IGameState;
import java.util.List;
import java.util.Objects;

public record JokerContext(
    Combination combo,
    List<Card> activeCards,
    List<Card> allSelectedCards,
    int level,
    BlindConstraint constraint,
    IGameState state,
    boolean isPreview) {
  public JokerContext {
    Objects.requireNonNull(combo);
    Objects.requireNonNull(activeCards);
    Objects.requireNonNull(allSelectedCards);
    Objects.requireNonNull(state);
    if (level < 0) {
      throw new IllegalArgumentException();
    }
  }
}
