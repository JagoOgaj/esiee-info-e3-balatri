package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.interfaces.IDeckManager;
import esiee.info.e3.model.interfaces.IHandEvaluator;
import esiee.info.e3.model.interfaces.IScoreCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameModel {
    private final IDeckManager deckManager;
    private final IHandEvaluator evaluator;
    private final IScoreCalculator scoreCalculator;
    private final GameState state;
    private final List<Card> currentHand;

    public GameModel(IDeckManager dm, IHandEvaluator he, IScoreCalculator sc) {
        this.deckManager = Objects.requireNonNull(dm);
        this.evaluator = Objects.requireNonNull(he);
        this.scoreCalculator = Objects.requireNonNull(sc);
        this.state = new GameState();
        this.currentHand = new ArrayList<>();
    }

    public void startRound() {
        this.deckManager.shuffle();
        this.currentHand.clear();
        this.refillHand();
    }

    public void resetGame() {
        this.state.resetGame();
        this.deckManager.discard(List.copyOf(this.currentHand));
        this.startRound();
    }

    public EvaluatedHand evaluateSelection(List<Card> selected) {
        if (selected == null || selected.isEmpty()) {
            return null;
        }
        var combo = this.evaluator.evaluate(selected);
        var level = this.state.getLevel(combo);
        var baseChips = this.scoreCalculator.computeBaseChips(combo, level);
        var multiplier = this.scoreCalculator.computeMultiplier(combo, level);
        return new EvaluatedHand(combo, baseChips, multiplier, level);
    }

    public long playHand(List<Card> selected) {
        var list = Objects.requireNonNull(selected);
        this.validateSelection(list);

        var combo = this.evaluator.evaluate(list);
        var level = this.state.getLevel(combo);
        var points = this.scoreCalculator.calculateScore(combo, list, level);

        this.state.addScore(points);
        this.state.useHand();
        this.processCardsExchange(list);

        return points;
    }

    public void discardCards(List<Card> selected) {
        if (this.state.getDiscardsLeft() <= 0) {
            throw new IllegalStateException();
        }
        var list = Objects.requireNonNull(selected);
        this.validateSelection(list);
        this.state.useDiscard();
        this.processCardsExchange(list);
    }

    private void processCardsExchange(List<Card> selected) {
        var cardsToDiscard = List.copyOf(selected);
        this.deckManager.discard(cardsToDiscard);
        cardsToDiscard.forEach(this.currentHand::remove);
        this.refillHand();
    }

    private void refillHand() {
        var needed = 8 - this.currentHand.size();
        if (needed > 0) {
            this.currentHand.addAll(this.deckManager.draw(needed));
        }
        this.state.setDeckSize(this.deckManager.getRemainingCount());
    }

    private void validateSelection(List<Card> selected) {
        if (selected.isEmpty()) {
            throw new IllegalArgumentException("Empty Selection");
        }
        if (selected.size() > 5) {
            throw new IllegalArgumentException("Max 5 cards");
        }
    }

    public boolean nextBlind() {
        if (this.state.nextBlind()) {
            this.state.resetForNewBlind();
            this.deckManager.discard(List.copyOf(this.currentHand));
            this.startRound();
            return true;
        }
        return false;
    }

    public GameState getState() {
        return this.state;
    }

    public List<Card> getCurrentHand() {
        return List.copyOf(this.currentHand);
    }
}