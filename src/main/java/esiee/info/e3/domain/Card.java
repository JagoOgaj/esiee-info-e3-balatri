package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import java.util.Objects;

public record Card(Rank rank, Suit suit) {
  public Card {
    Objects.requireNonNull(rank);
    Objects.requireNonNull(suit);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    return o instanceof Card(Rank rank1, Suit suit1) && this.rank == rank1 && this.suit == suit1;
  }

  @Override
  public int hashCode() {
    return Objects.hash(rank, suit);
  }

  @Override
  public String toString() {
    return "Card{" + "rank=" + rank + ", suit=" + suit + '}';
  }
}
