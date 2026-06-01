package esiee.info.e3.model;

import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import java.util.List;
import java.util.Set;

public sealed interface IGameState permits GameState {
  Blind getCurrentBlind();

  boolean nextBlind();

  void resetForNewBlind();

  void resetGame();

  boolean isInfiniteMode();

  void setInfiniteMode(boolean infiniteMode);

  int getLoopCount();

  void setLoopCount(int loopCount);

  int getMoney();

  void setMoney(int money);

  void addMoney(int amount);

  void spendMoney(int amount);

  void addJoker(JokerType joker);

  List<JokerType> getActiveJokers();

  boolean removeJoker(JokerType joker);

  boolean isJokersFull();

  String getCurrentSaveId();

  void setCurrentSaveId(String saveId);

  void addScore(long points);

  int getLevel(Combination combination);

  void upgradeHand(Combination combination);

  void addWonPlanet(Planet planet);

  boolean hasPlanet(Planet planet);

  Set<Planet> getWonPlanets();

  void loadState(
      long score,
      int hands,
      int discards,
      int deckSize,
      int blindIndex,
      boolean isInfinite,
      int loop);

  void setLevel(Combination combo, int level);

  BlindConstraint getCurrentConstraint();

  void setCurrentConstraint(BlindConstraint constraint);

  int getCurrentBlindIndex();

  long getCurrentScore();

  int getHandsLeft();

  void useHand();

  int getDiscardsLeft();

  void useDiscard();

  int getDeckSize();

  void setDeckSize(int deckSize);
}
