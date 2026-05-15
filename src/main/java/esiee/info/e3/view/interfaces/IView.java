package esiee.info.e3.view.interfaces;

import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.GameState;

import java.util.List;

public interface IView {
  void setController(GameController controller);

  void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval);

  void showMessage(String message);

  void showError(String error);

  void showGameOver(boolean victory, long finalScore);

  void start();
}
