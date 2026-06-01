package esiee.info.e3.controller;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.JokerType;

public sealed interface IGameController permits GameController {
  void startGame(boolean infiniteMode);

  void init();

  void resetGame();

  void loadGameFromJson(String saveId);

  void toggleCardSelection(Card card);

  void handlePlay();

  void handleDiscard();

  String getJokerRarity(JokerType joker);

  long getExpectedScore();

  void saveAndQuit();

  void exitGame();

  long getHighScore();
}
