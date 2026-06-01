package esiee.info.e3.model;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.view.IView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class GameModel implements IGameModel {
  private final IDeckManager deckManager;
  private final IHandEvaluator evaluator;
  private final IScoreCalculator scoreCalculator;
  private final IGameState state;
  private final List<Card> currentHand;
  private final BlindConstraint[] hard;
  private final JokerRewardService rewardService;
  private final List<IView> observers;
  private final List<Card> selectedCards;

  public GameModel(List<Blind> blinds) {
    this.deckManager = new DeckManager();
    this.evaluator = new HandEvaluator();
    this.scoreCalculator = new ScoreCalculator();
    this.state = new GameState(Objects.requireNonNull(blinds));
    this.currentHand = new ArrayList<>();
    this.hard =
        new BlindConstraint[] {
          BlindConstraint.THE_HOOK, BlindConstraint.THE_MANACLE, BlindConstraint.THE_HOUSE
        };
    this.rewardService = new JokerRewardService(ThreadLocalRandom.current());
    this.observers = new ArrayList<>();
    this.selectedCards = new ArrayList<>();
  }

  @Override
  public void addObserver(IView observer) {
    this.observers.add(Objects.requireNonNull(observer));
  }

  @Override
  public void notifyObservers() {
    var eval = this.selectedCards.isEmpty() ? null : this.evaluateHand();
    var snapshot = new GameSnapshot(this.state, this.getHand(), this.getSelectedCards(), eval);
    for (var observer : observers) {
      observer.onModelUpdated(snapshot);
    }
  }

  @Override
  public void executeStartGameAction(boolean infiniteMode) {
    this.resetSelectedCards();
    this.resetGame();
    this.state.setInfiniteMode(infiniteMode);
    this.notifyObservers();
  }

  @Override
  public void executeDiscardAction() {
    if (this.state.getDiscardsLeft() <= 0) return;
    this.discardHand(new ArrayList<>(this.getSelectedCards()));
    this.resetSelectedCards();
    this.notifyObservers();
  }

  @Override
  public TurnResult executePlayAction() {
    if (this.isEmptySelectedCards()) {
      return new TurnResultError(TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText());
    }
    if (this.state.getHandsLeft() <= 0)
      return new TurnResultError(TextConstant.TEXT_CONSTANT_NO_HAND_AVAILABLE.getText());

    try {
      var scoreGained = this.playHand(new ArrayList<>(this.getSelectedCards()));
      this.resetSelectedCards();

      if (this.state.getCurrentScore() >= this.state.getCurrentBlind().score()) {
        var wonPlanet = this.grantRandomPlanetReward();
        var baseReward = 4;
        var handsBonus = this.state.getHandsLeft();
        var discardsBonus = this.state.getDiscardsLeft();
        var totalMoney = baseReward + handsBonus + discardsBonus;
        this.state.addMoney(totalMoney);

        if (this.nextBlind()) {
          var nextBlind = this.state.getCurrentBlind();
          var constraint = this.state.getCurrentConstraint();
          String constraintText =
              (constraint != null && !constraint.name().equals("NONE"))
                  ? "\n"
                      + TextConstant.TEXT_CONSTANT_PREFIX_CONSTRAINT
                      + " "
                      + constraint.getDescription()
                  : "";

          String msg =
              TextConstant.TEMPLATE_ROUND_REWARD_MESSAGE
                  .getText()
                  .formatted(
                      totalMoney,
                      nextBlind.id(),
                      wonPlanet.getFileName(),
                      TextConstant.TEXT_CONSTANT_BLIND_BEATEN.getText(),
                      nextBlind.name(),
                      constraintText,
                      baseReward,
                      handsBonus,
                      discardsBonus,
                      totalMoney);

          this.notifyObservers();
          return new TurnResultBlindBeaten(totalMoney, msg);
        } else {
          this.notifyObservers();
          return new TurnResultGameWon();
        }
      } else if (this.state.getHandsLeft() <= 0) {
        this.notifyObservers();
        return new TurnResultGameLost();
      } else {
        this.notifyObservers();
        return new TurnResultHandPlayed(scoreGained);
      }
    } catch (Exception e) {
      return new TurnResultFailure(e);
    }
  }

  @Override
  public void startRound() {
    this.deckManager.shuffle();
    this.currentHand.clear();
    this.currentHand.addAll(this.deckManager.draw(8));
    this.refillHand();
  }

  @Override
  public void resetGame() {
    this.resetSelectedCards();
    this.state.resetGame();
    this.deckManager.discard(List.copyOf(this.currentHand));
    this.startRound();
    this.notifyObservers();
  }

  @Override
  public void resetSelectedCards() {
    this.selectedCards.clear();
  }

  @Override
  public boolean isEmptySelectedCards() {
    return this.selectedCards.isEmpty();
  }

  @Override
  public List<Card> getSelectedCards() {
    return List.copyOf(this.selectedCards);
  }

  @Override
  public boolean toggleCardSelection(Card card) {
    var selectedCard = Objects.requireNonNull(card);
    boolean altered;
    if (this.selectedCards.contains(selectedCard)) {
      this.selectedCards.remove(selectedCard);
      altered = true;
    } else {
      if (this.selectedCards.size() < 5) {
        this.selectedCards.add(selectedCard);
        altered = true;
      } else {
        altered = false;
      }
    }
    if (altered) {
      this.notifyObservers();
    }
    return altered;
  }

  @Override
  public EvaluatedHand evaluateHand() {
    if (this.isEmptySelectedCards()) return null;
    var combo = this.evaluator.evaluate(this.getSelectedCards());
    var level = this.state.getLevel(combo);
    var baseChips = this.scoreCalculator.computeBaseChips(combo, level);
    var multiplier = this.scoreCalculator.computeMultiplier(combo, level);
    return new EvaluatedHand(combo, baseChips, multiplier, level);
  }

  @Override
  public long playHand(List<Card> selected) {
    var list = Objects.requireNonNull(selected);
    this.validateSelection(list);

    var combo = this.evaluator.evaluate(list);
    var level = this.state.getLevel(combo);

    var points =
        this.scoreCalculator.calculateScore(
            combo,
            list,
            level,
            this.state.getCurrentConstraint(),
            this.state,
            this.state.getActiveJokers(),
            false);

    this.state.addScore(points);
    this.state.useHand();
    this.processCardsExchange(list);

    return points;
  }

  @Override
  public long calculateExpectedScore() {
    if (this.isEmptySelectedCards()) return 0;
    var combo = this.evaluator.evaluate(this.getSelectedCards());
    var level = this.state.getLevel(combo);

    return this.scoreCalculator.calculateScore(
        combo,
        this.getSelectedCards(),
        level,
        this.state.getCurrentConstraint(),
        this.state,
        this.state.getActiveJokers(),
        true);
  }

  @Override
  public String getJokerRarityLabel(JokerType joker) {
    Objects.requireNonNull(joker);
    return this.rewardService.getJokerRarity(joker).name();
  }

  @Override
  public boolean removeJoker(JokerType joker) {
    Objects.requireNonNull(joker);
    var res = this.state.removeJoker(joker);
    if (res) this.notifyObservers();
    return res;
  }

  @Override
  public void discardHand(List<Card> selected) {
    if (this.state.getDiscardsLeft() <= 0) throw new IllegalStateException();
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
      for (var i = 0; i < toDiscard; i++) {
        var randIdx = ThreadLocalRandom.current().nextInt(this.currentHand.size());
        var c = this.currentHand.remove(randIdx);
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
    if (selected.isEmpty()) throw new IllegalArgumentException("Empty selection");
    if (selected.size() > 5) throw new IllegalArgumentException("Max 5 cards");
  }

  @Override
  public Planet grantRandomPlanetReward() {
    var allPlanets = Planet.values();
    var randomIndex = ThreadLocalRandom.current().nextInt(allPlanets.length);
    var wonPlanet = allPlanets[randomIndex];
    this.state.upgradeHand(wonPlanet.getCombination());
    this.state.addWonPlanet(wonPlanet);
    return wonPlanet;
  }

  @Override
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
    BlindConstraint[] easy = {
      BlindConstraint.THE_CLUB,
      BlindConstraint.THE_GOAD,
      BlindConstraint.THE_WINDOW,
      BlindConstraint.THE_HEAD
    };
    return easy[ThreadLocalRandom.current().nextInt(easy.length)];
  }

  private BlindConstraint getRandomHardConstraint() {
    return this.hard[ThreadLocalRandom.current().nextInt(hard.length)];
  }

  @Override
  public void loadHand(List<Card> loadedHand) {
    this.currentHand.clear();
    this.currentHand.addAll(Objects.requireNonNull(loadedHand));
  }

  @Override
  public IGameState getState() {
    return this.state;
  }

  @Override
  public List<Card> getHand() {
    return List.copyOf(this.currentHand);
  }
}
