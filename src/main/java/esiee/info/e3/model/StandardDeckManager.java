package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.model.interfaces.IDeckManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StandardDeckManager implements IDeckManager {
  private final List<Card> drawPile;
  private final List<Card> discardPile;

  public StandardDeckManager() {
    this.drawPile = new ArrayList<>();
    this.discardPile = new ArrayList<>();
    for (var s : Suit.values()) {
      for (var r : Rank.values()) {
        this.drawPile.add(new Card(r, s));
      }
    }
    this.shuffle();
  }

  @Override
  public void shuffle() {
    Collections.shuffle(this.drawPile);
  }

  @Override
  public List<Card> draw(int count) {
    var drawn = new ArrayList<Card>();
    for (var i = 0; i < count; i++) {
      if (this.drawPile.isEmpty()) {
        this.recycleDiscardPile();
      }
      if (!this.drawPile.isEmpty()) {
        drawn.add(this.drawPile.removeFirst());
      }
    }
    return drawn;
  }

  @Override
  public void discard(List<Card> cards) {
    this.discardPile.addAll(Objects.requireNonNull(cards));
  }

  @Override
  public int getRemainingCount() {
    return this.drawPile.size();
  }

  private void recycleDiscardPile() {
    this.drawPile.addAll(this.discardPile);
    this.discardPile.clear();
    this.shuffle();
  }
}
