package esiee.info.e3.view.interfaces;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.GameState;
import esiee.info.e3.controller.GameController;
import java.util.List;
import java.util.function.Consumer;

public interface IView {
    void setController(GameController controller);
    void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval);
    void start();
    void showError(String message);
    void showMessage(String message);
    void showGameOver(boolean won, long finalScore);
    void showMenu();
    void triggerJokerReplacement(JokerType newJoker, Runnable onCancel, Consumer<JokerType> onConfirm);
}