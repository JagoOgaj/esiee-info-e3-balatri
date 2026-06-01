package esiee.info.e3.view;

import esiee.info.e3.controller.IGameController;
import esiee.info.e3.domain.GameSnapshot;

public sealed interface IView permits ViewMain {
  void setController(IGameController controller);

  void update(GameSnapshot gameSnapshot);

  void start();

  void showError(String message);

  void showMessage(String message);

  void showGameOver(boolean won, long finalScore);

  void showMenu();

  void onModelUpdated(GameSnapshot snapshot);
}
