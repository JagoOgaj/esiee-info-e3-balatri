package esiee.info.e3.model;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.ShopItem;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import java.util.*;

public non-sealed class GameState implements IGameState {
  private final Map<Combination, Integer> handLevels;
  private final List<Blind> blinds;
  private final Set<Planet> wonPlanets;
  private final Set<JokerType> activeJokers;
  private final List<ShopItem> currentShopItems;
  private final List<ShopItem> sessionPurchases;
  private long currentScore;
  private int handsLeft;
  private int discardsLeft;
  private int deckSize;
  private int currentBlindIndex;
  private BlindConstraint currentConstraint;
  private String currentSaveId;
  private int money;
  private boolean infiniteMode;
  private int loopCount;

  public GameState(List<Blind> blinds) {
    this.handLevels = new EnumMap<>(Combination.class);
    this.blinds = Objects.requireNonNull(blinds);
    this.wonPlanets = new HashSet<>();
    this.activeJokers = new LinkedHashSet<>();
    this.currentShopItems = new ArrayList<>();
    this.sessionPurchases = new ArrayList<>();
    this.money = 4;
    this.infiniteMode = false;
    this.currentConstraint = BlindConstraint.NONE;
    this.loopCount = 0;
    this.resetGame();
  }

  @Override
  public Blind getCurrentBlind() {
    var baseBlind = this.blinds.get(this.currentBlindIndex);
    if (this.infiniteMode && this.loopCount > 0) {
      var scaledScore = baseBlind.score() * (long) Math.pow(10, this.loopCount);
      return new Blind(
          baseBlind.id(),
          baseBlind.name() + TextConstant.TEXT_CONSTANT_LOOP.getText().formatted(this.loopCount),
          scaledScore);
    }
    return baseBlind;
  }

  @Override
  public boolean nextBlind() {
    if (this.currentBlindIndex < this.blinds.size() - 1) {
      this.currentBlindIndex++;
      return true;
    }
    if (this.infiniteMode) {
      this.currentBlindIndex = 0;
      this.loopCount++;
      return true;
    }
    return false;
  }

  @Override
  public void resetForNewBlind() {
    this.currentScore = 0;
    this.handsLeft = 4;
    this.discardsLeft = 3;
  }

  @Override
  public void resetGame() {
    this.currentBlindIndex = 0;
    this.deckSize = 52;
    this.currentSaveId = null;
    this.loopCount = 0;
    this.money = 4;
    this.currentShopItems.clear();
    this.sessionPurchases.clear();
    this.resetForNewBlind();
    for (var combination : Combination.values()) {
      this.handLevels.put(combination, 1);
    }
    this.wonPlanets.clear();
    this.activeJokers.clear();
    this.currentConstraint = BlindConstraint.NONE;
  }

  @Override
  public List<ShopItem> getShopItems() {
    return this.currentShopItems;
  }

  @Override
  public void setShopItems(List<ShopItem> items) {
    this.currentShopItems.clear();
    this.currentShopItems.addAll(items);
  }

  @Override
  public List<ShopItem> getSessionPurchases() {
    return this.sessionPurchases;
  }

  @Override
  public void clearShopAndPurchases() {
    this.currentShopItems.clear();
    this.sessionPurchases.clear();
  }

  @Override
  public boolean isInfiniteMode() {
    return infiniteMode;
  }

  @Override
  public void setInfiniteMode(boolean infiniteMode) {
    this.infiniteMode = infiniteMode;
  }

  @Override
  public int getLoopCount() {
    return loopCount;
  }

  @Override
  public void setLoopCount(int loopCount) {
    this.loopCount = loopCount;
  }

  @Override
  public int getMoney() {
    return money;
  }

  @Override
  public void setMoney(int money) {
    this.money = money;
  }

  @Override
  public void addMoney(int amount) {
    this.money += amount;
  }

  @Override
  public void spendMoney(int amount) {
    if (this.money >= amount) {
      this.money -= amount;
    }
  }

  @Override
  public List<JokerType> getActiveJokers() {
    return List.copyOf(this.activeJokers);
  }

  @Override
  public void addJoker(JokerType joker) {
    Objects.requireNonNull(joker);
    if (this.activeJokers.contains(joker)) {
      return;
    }
    if (this.activeJokers.size() < 5) {
      this.activeJokers.add(joker);
    }
  }

  @Override
  public boolean removeJoker(JokerType joker) {
    Objects.requireNonNull(joker);
    return this.activeJokers.remove(joker);
  }

  @Override
  public boolean isJokersFull() {
    return this.activeJokers.size() >= 5;
  }

  @Override
  public String getCurrentSaveId() {
    return currentSaveId;
  }

  @Override
  public void setCurrentSaveId(String saveId) {
    this.currentSaveId = Objects.requireNonNull(saveId);
  }

  @Override
  public void addScore(long points) {
    this.currentScore += points;
  }

  @Override
  public int getLevel(Combination combination) {
    return this.handLevels.getOrDefault(Objects.requireNonNull(combination), 1);
  }

  @Override
  public void upgradeHand(Combination combination) {
    var combo = Objects.requireNonNull(combination);
    this.handLevels.put(combo, this.getLevel(combo) + 1);
  }

  @Override
  public void addWonPlanet(Planet planet) {
    Objects.requireNonNull(planet);
    this.wonPlanets.add(planet);
  }

  @Override
  public boolean hasPlanet(Planet planet) {
    Objects.requireNonNull(planet);
    return this.wonPlanets.contains(planet);
  }

  @Override
  public Set<Planet> getWonPlanets() {
    return Set.copyOf(this.wonPlanets);
  }

  @Override
  public void loadState(
      long score,
      int hands,
      int discards,
      int deckSize,
      int blindIndex,
      boolean isInfinite,
      int loop) {
    this.currentScore = score;
    this.handsLeft = hands;
    this.discardsLeft = discards;
    this.deckSize = deckSize;
    this.currentBlindIndex = blindIndex;
    this.infiniteMode = isInfinite;
    this.loopCount = loop;
  }

  @Override
  public void setLevel(Combination combo, int level) {
    Objects.requireNonNull(combo);
    this.handLevels.put(combo, level);
  }

  @Override
  public BlindConstraint getCurrentConstraint() {
    return this.currentConstraint;
  }

  @Override
  public void setCurrentConstraint(BlindConstraint constraint) {
    this.currentConstraint = Objects.requireNonNull(constraint);
  }

  @Override
  public int getCurrentBlindIndex() {
    return this.currentBlindIndex;
  }

  @Override
  public long getCurrentScore() {
    return this.currentScore;
  }

  @Override
  public int getHandsLeft() {
    return this.handsLeft;
  }

  @Override
  public void useHand() {
    this.handsLeft--;
  }

  @Override
  public int getDiscardsLeft() {
    return this.discardsLeft;
  }

  @Override
  public void useDiscard() {
    this.discardsLeft--;
  }

  @Override
  public int getDeckSize() {
    return this.deckSize;
  }

  @Override
  public void setDeckSize(int deckSize) {
    this.deckSize = deckSize;
  }
}
