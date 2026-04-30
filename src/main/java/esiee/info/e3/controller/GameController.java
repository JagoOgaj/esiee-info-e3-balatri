package esiee.info.e3.controller;

import esiee.info.e3.domain.Card;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.view.IView;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final GameModel model;
    private final IView view;
    private final List<Card> selectedCards = new ArrayList<>();

    public GameController(GameModel model, IView view) {
        this.model = model;
        this.view = view;
    }

    public void init() {
        model.startRound();
        refreshView();
        view.start();
    }

    public void toggleCardSelection(Card card) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
        } else {
            if (selectedCards.size() < 5) {
                selectedCards.add(card);
            } else {
                view.showError("Vous ne pouvez sélectionner que 5 cartes maximum.");
            }
        }
        refreshView();
    }

    public void handlePlay() {
        if (selectedCards.isEmpty()) {
            view.showError("Sélectionnez au moins une carte pour jouer.");
            return;
        }

        try {
            var scoreGained = model.playHand(new ArrayList<>(selectedCards));
            view.showMessage("Main jouée ! +" + scoreGained + " points.");

            selectedCards.clear();
            checkGameOver();
            refreshView();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleDiscard() {
        if (selectedCards.isEmpty()) {
            view.showError("Sélectionnez les cartes à défausser.");
            return;
        }

        try {
            model.discardCards(new ArrayList<>(selectedCards));
            selectedCards.clear();
            refreshView();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    private void checkGameOver() {
        if (model.getState().getHandsLeft() <= 0) {
            view.showGameOver(model.getState().getCurrentScore());
        }
    }

    private void refreshView() {
        view.update(model.getState(), model.getCurrentHand(), List.copyOf(selectedCards));
    }
}