package esiee.info.e3.model;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.interfaces.IDeckManager;
import esiee.info.e3.model.interfaces.IHandEvaluator;
import esiee.info.e3.model.interfaces.IScoreCalculator;
import esiee.info.e3.model.interfaces.ModelObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameModel {
    private final IDeckManager deckManager;
    private final IHandEvaluator evaluator;
    private final IScoreCalculator scoreCalculator;
    private final GameState state;
    private final List<Card> currentHand;
    private final BlindConstraint[] hard;
    private final JokerRewardService rewardService;
    private final List<ModelObserver> observers;
    private final List<Card> selectedCards;

    public GameModel(IDeckManager dm, IHandEvaluator he, IScoreCalculator sc) {
        this.deckManager = Objects.requireNonNull(dm);
        this.evaluator = Objects.requireNonNull(he);
        this.scoreCalculator = Objects.requireNonNull(sc);
        this.state = new GameState();
        this.currentHand = new ArrayList<>();
        this.hard = new BlindConstraint[]{ BlindConstraint.THE_HOOK, BlindConstraint.THE_MANACLE, BlindConstraint.THE_HOUSE };
        this.rewardService = new JokerRewardService(ThreadLocalRandom.current());
        this.observers = new ArrayList<>();
        this.selectedCards = new ArrayList<>();
    }

    public void addObserver(ModelObserver observer) {
        this.observers.add(Objects.requireNonNull(observer));
    }

    public void notifyObservers() {
        EvaluatedHand eval = this.selectedCards.isEmpty() ? null : this.evaluateHand();
        for (ModelObserver observer : observers) {
            observer.onModelUpdated(new GameSnapshot(this.state, this.getHand(), this.getSelectedCards(), eval));
        }
    }

    public void startRound() {
        this.deckManager.shuffle();
        this.currentHand.clear();
        this.currentHand.addAll(this.deckManager.draw(8));
        this.refillHand();
    }

    public void resetGame() {
        this.resetSelectedCards();
        this.state.resetGame();
        this.deckManager.discard(List.copyOf(this.currentHand));
        this.startRound();
    }

    public void resetSelectedCards() {
        this.selectedCards.clear();
    }

    public boolean isEmptySelectedCards() {
        return this.selectedCards.isEmpty();
    }

    public List<Card> getSelectedCards() {
        return List.copyOf(this.selectedCards);
    }

    public boolean toggleCardSelection(Card card) {
        var selectedCard = Objects.requireNonNull(card);
        if (this.selectedCards.contains(selectedCard)) {
            this.selectedCards.remove(selectedCard);
            return true;
        } else {
            if (this.selectedCards.size() < 5) {
                this.selectedCards.add(selectedCard);
                return true;
            }
            else {
                return false;
            }
        }
    }

    public EvaluatedHand evaluateHand() {
        if (this.selectedCards == null || this.isEmptySelectedCards()) {
            return null;
        }
        var combo = this.evaluator.evaluate(this.getSelectedCards());
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

        var points = this.scoreCalculator.calculateScore(
                combo, list, level, this.state.getCurrentConstraint(),
                this.state, this.state.getActiveJokers()
        );

        this.state.addScore(points);
        this.state.useHand();
        this.processCardsExchange(list);

        return points;
    }

    public long calculateExpectedScore() {
        if (this.selectedCards == null || this.isEmptySelectedCards()) return 0;
        var combo = this.evaluator.evaluate(this.getSelectedCards());
        var level = this.state.getLevel(combo);

        return this.scoreCalculator.calculateScore(
                combo, this.getSelectedCards(), level, this.state.getCurrentConstraint(),
                this.state, this.state.getActiveJokers()
        );
    }


    public JokerType rollJokerReward(int blindIndex) {
        return this.rewardService.rollJokerReward(blindIndex, this.state.getActiveJokers());
    }

    public String getJokerRarityLabel(JokerType joker) {
        return this.rewardService.getJokerRarity(joker).name();
    }

    public boolean addJoker(JokerType joker) {
        return this.state.addJoker(joker);
    }

    public boolean removeJoker(JokerType joker) {
        return this.state.removeJoker(joker);
    }

    public void discardHand(List<Card> selected) {
        if (this.state.getDiscardsLeft() <= 0) {
            throw new IllegalStateException();
        }
        var list = Objects.requireNonNull(selected);
        this.validateSelection(list);
        this.state.useDiscard();
        this.processCardsExchange(list);
    }

    private void processCardsExchange(List<Card> selected) {
        var cardsToDiscard = new ArrayList<>(selected);
        this.deckManager.discard(cardsToDiscard);
        cardsToDiscard.forEach(this.currentHand::remove);

        if (this.state.getCurrentConstraint() == BlindConstraint.THE_HOOK) {
            int toDiscard = Math.min(2, this.currentHand.size());
            for (int i = 0; i < toDiscard; i++) {
                int randIdx = ThreadLocalRandom.current().nextInt(this.currentHand.size());
                Card c = this.currentHand.remove(randIdx);
                this.deckManager.discard(List.of(c));
            }
        }
        this.refillHand();
    }

    private void refillHand() {
        var needed = 8 - this.currentHand.size();
        if (this.state.getCurrentConstraint() == BlindConstraint.THE_MANACLE) {
            needed = 7 - this.currentHand.size();
        }
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

    public Planet grantRandomPlanetReward() {
        var allPlanets = Planet.values();
        var randomIndex = ThreadLocalRandom.current().nextInt(allPlanets.length);
        var wonPlanet = allPlanets[randomIndex];
        this.state.upgradeHand(wonPlanet.getCombination());
        this.state.addWonPlanet(wonPlanet);
        return wonPlanet;
    }

    public boolean nextBlind() {
        if (this.state.nextBlind()) {
            this.state.resetForNewBlind();
            this.assignBossConstraint();
            this.deckManager.discard(List.copyOf(this.currentHand));
            this.startRound();
            return true;
        }
        return false;
    }

    private void assignBossConstraint() {
        int index = this.state.getCurrentBlindIndex();
        if ((index + 1) % 3 == 0) {
            int random = ThreadLocalRandom.current().nextInt(100);
            if (index < 9) {
                this.state.setCurrentConstraint(getRandomEasyConstraint());
            } else if (index < 18) {
                if (random < 50) this.state.setCurrentConstraint(getRandomEasyConstraint());
                else this.state.setCurrentConstraint(getRandomHardConstraint());
            } else {
                if (random < 20) this.state.setCurrentConstraint(getRandomEasyConstraint());
                else this.state.setCurrentConstraint(getRandomHardConstraint());
            }
        } else {
            this.state.setCurrentConstraint(BlindConstraint.NONE);
        }
    }

    private BlindConstraint getRandomEasyConstraint() {
        BlindConstraint[] easy = { BlindConstraint.THE_CLUB, BlindConstraint.THE_GOAD, BlindConstraint.THE_WINDOW, BlindConstraint.THE_HEAD };
        return easy[ThreadLocalRandom.current().nextInt(easy.length)];
    }

    private BlindConstraint getRandomHardConstraint() {
        return this.hard[ThreadLocalRandom.current().nextInt(hard.length)];
    }

    public void loadHand(List<Card> loadedHand) {
        this.currentHand.clear();
        this.currentHand.addAll(loadedHand);
    }

    public GameState getState() {
        return this.state;
    }

    public List<Card> getHand() {
        return List.copyOf(this.currentHand);
    }
}