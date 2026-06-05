package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.ShopItem;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.view.IView;
import java.util.List;

public sealed interface IGameModel permits GameModel {
  void addObserver(IView observer);

  void notifyObservers();

  void startRound();

  void resetGame();

  void resetSelectedCards();

  boolean isEmptySelectedCards();

  List<Card> getSelectedCards();

  boolean toggleCardSelection(Card card);

  EvaluatedHand evaluateHand();

  long playHand(List<Card> selected);

  long calculateExpectedScore();

  String getJokerRarityLabel(JokerType joker);

  void discardHand(List<Card> selected);

  Planet grantRandomPlanetReward();

  boolean nextBlind();

  void loadHand(List<Card> loadedHand);

  IGameState getState();

  List<Card> getHand();

  void executeStartGameAction(boolean infiniteMode);

  void executeDiscardAction();

  TurnResult executePlayAction();

  void rollShopItems();

  void executeRerollShopAction();

  void executeBuyShopItemAction(int index);

  void executeSwapJokerAndBuyAction(JokerType oldJoker, int shopIndex);

  void executeRefundShopItemAction(ShopItem si);

  void executeLeaveShopAction();

  boolean isGameActive();
}
