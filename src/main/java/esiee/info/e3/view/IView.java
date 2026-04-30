package esiee.info.e3.view;

import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.model.GameState;

import java.util.List;

public interface IView {
    void setController(GameController controller);
    void update(GameState state, List<Card> hand, List<Card> selectedCards);
    void showMessage(String message);
    void showError(String error);
    void showGameOver(long finalScore);
    void start();
}
