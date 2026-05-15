package esiee.info.e3.controller;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Card;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.view.interfaces.IView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameController {
    private final GameModel model;
    private final IView view;
    private final List<Card> selectedCards;

    public GameController(GameModel model, IView view) {
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
        this.selectedCards = new ArrayList<>();
    }

    public void init() {
        this.model.startRound();
        this.refreshView();
        this.view.start();
    }

    public void resetGame() {
        this.selectedCards.clear();
        this.model.resetGame();
        this.refreshView();
    }

    public void toggleCardSelection(Card card) {
        var selectedCard = Objects.requireNonNull(card);
        if (this.selectedCards.contains(selectedCard)) {
            this.selectedCards.remove(selectedCard);
        } else {
            if (this.selectedCards.size() < 5) {
                this.selectedCards.add(selectedCard);
            } else {
                this.view.showError(TextConstant.TEXT_CONSTANT_ERROR_MAX_CARDS.getText());
            }
        }
        this.refreshView();
    }

    public void handlePlay(Optional<Character> actionFromConsole) {
        if (actionFromConsole.equals(Optional.of('Q'))) {
            IO.println("Merci d'avoir joué à Balatri !");
            System.exit(0);
        }
        if (this.selectedCards.isEmpty()) {
            return;
        }
        if (this.model.getState().getHandsLeft() <= 0) {
            return;
        }

        try {
            var eval = Objects.requireNonNull(this.model.evaluateSelection(this.selectedCards));
            var scoreGained = this.model.playHand(new ArrayList<>(this.selectedCards));
            this.selectedCards.clear();

            var state = this.model.getState();

            if (state.getCurrentScore() >= state.getCurrentBlind().score()) {
                state.upgradeHand(eval.combo());
                if (this.model.nextBlind()) {

                    var msg =
                            TextConstant.TEXT_CONSTANT_BLIND_BEATEN.getText()
                                    + eval.combo().getLabel()
                                    + TextConstant.TEXT_CONSTANT_LEVEL_INCREASED.getText();
                    this.view.showMessage(msg);
                } else {
                    this.view.showGameOver(true, state.getCurrentScore());
                }
            } else if (state.getHandsLeft() <= 0) {
                this.view.showGameOver(false, state.getCurrentScore());
            } else {
                this.view.showMessage(
                        TextConstant.TEXT_CONSTANT_HAND_PLAYED.getText()
                                + scoreGained
                                + TextConstant.TEXT_CONSTANT_POINTS.getText());
            }

            this.refreshView();
        } catch (Exception e) {
            this.view.showError(e.getMessage());
        }
    }

    public void handleDiscard() {
        if (this.selectedCards.isEmpty()) {
            view.showError(TextConstant.TEXT_CONSTANT_ERROR_DISCARD_EMPTY.getText());
            return;
        }
        try {
            this.model.discardCards(new ArrayList<>(this.selectedCards));
            this.selectedCards.clear();
            this.refreshView();
        } catch (Exception e) {
            this.view.showError(e.getMessage());
        }
    }

    public void refreshView() {
        var eval = this.model.evaluateSelection(this.selectedCards);
        this.view.update(
                this.model.getState(), this.model.getCurrentHand(), List.copyOf(this.selectedCards), eval);
    }
}
