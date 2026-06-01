package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import java.util.Objects;

public record Card(Rank rank, Suit suit) {
  public Card {
    Objects.requireNonNull(rank);
    Objects.requireNonNull(suit);
  }
}
