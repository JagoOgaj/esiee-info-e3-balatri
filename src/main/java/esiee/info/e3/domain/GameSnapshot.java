package esiee.info.e3.domain;

import esiee.info.e3.model.IGameState;
import java.util.List;
import java.util.Objects;

public record GameSnapshot(
    IGameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand evaluation) {
  public GameSnapshot {
    Objects.requireNonNull(state);
    Objects.requireNonNull(hand);
    Objects.requireNonNull(selectedCards);
  }
}
