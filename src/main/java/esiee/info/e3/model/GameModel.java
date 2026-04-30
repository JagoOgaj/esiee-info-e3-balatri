package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.Combination;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final IDeckManager deckManager;
    private final IHandEvaluator evaluator;
    private final IScoreCalculator scoreCalculator;

    private final GameState state;
    private final List<Card> currentHand = new ArrayList<>();
    private final List<IGameFeature> activeFeatures = new ArrayList<>();

    public GameModel(IDeckManager dm, IHandEvaluator he, IScoreCalculator sc) {
        this.deckManager = dm;
        this.evaluator = he;
        this.scoreCalculator = sc;
        this.state = new GameState();
    }

    public void startRound() {
        currentHand.clear();
        currentHand.addAll(deckManager.draw(8));
    }

    public long playHand(List<Card> selected) {
        if (selected.size() > 5) {
            throw new IllegalArgumentException("Max 5 cartes");
        }

        Combination combo = evaluator.evaluate(selected);
        var level = state.getLevel(combo);

        var basePoints = scoreCalculator.calculateScore(combo, selected, level);

        for (IGameFeature feature : activeFeatures) {
            basePoints = feature.applyModifier(basePoints, state);
        }

        state.addScore(basePoints);
        state.useHand();
        deckManager.discard(selected);
        currentHand.removeAll(selected);
        currentHand.addAll(deckManager.draw(selected.size()));

        return basePoints;
    }

    public GameState getState() { return state; }
    public List<Card> getCurrentHand() { return List.copyOf(currentHand); }

    public void discardCards(List<Card> selected) {
        if (state.getDiscardsLeft() <= 0) {
            throw new IllegalStateException("Vous n'avez plus de défausses disponibles !");
        }
        if (selected.size() > 5) {
            throw new IllegalArgumentException("Max 5 cartes à défausser");
        }

        state.useDiscard();
        deckManager.discard(selected);
        currentHand.removeAll(selected);
        currentHand.addAll(deckManager.draw(selected.size()));
    }
}