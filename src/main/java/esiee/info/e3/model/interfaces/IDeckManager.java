package esiee.info.e3.model.interfaces;

import esiee.info.e3.domain.Card;
import java.util.List;

public interface IDeckManager {
  void shuffle();

  List<Card> draw(int count);

  void discard(List<Card> cards);

  int getRemainingCount();
}
