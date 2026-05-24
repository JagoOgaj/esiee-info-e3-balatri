package esiee.info.e3.model;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.domain.enums.JokerType;
import java.util.*;

public class GameState {
    private final Map<Combination, Integer> handLevels;
    private final List<Blind> blinds;
    private final Set<Combination> playedCombinationsInCurrentBlind;
    private final Set<Planet> wonPlanets;
    private final Set<JokerType> activeJokers;

    private long currentScore;
    private int handsLeft;
    private int discardsLeft;
    private int deckSize;
    private int currentBlindIndex;
    private BlindConstraint currentConstraint = BlindConstraint.NONE;
    private String currentSaveId;

    private boolean infiniteMode = false;
    private int loopCount = 0;

    public GameState() {
        this.handLevels = new EnumMap<>(Combination.class);
        this.blinds = GameConfig.allBlinds();
        this.playedCombinationsInCurrentBlind = new HashSet<>();
        this.wonPlanets = new HashSet<>();
        this.activeJokers = new LinkedHashSet<>();
        this.resetGame();
    }

    public Blind getCurrentBlind() {
        Blind baseBlind = this.blinds.get(this.currentBlindIndex);
        if (this.infiniteMode && this.loopCount > 0) {
            long scaledScore = baseBlind.score() * (long) Math.pow(10, this.loopCount);
            return new Blind(baseBlind.id(), baseBlind.name() + " [Boucle " + this.loopCount + "]", scaledScore);
        }
        return baseBlind;
    }

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

    public void resetForNewBlind() {
        this.currentScore = 0;
        this.handsLeft = 4;
        this.discardsLeft = 3;
        this.playedCombinationsInCurrentBlind.clear();
    }

    public void resetGame() {
        this.currentBlindIndex = 0;
        this.deckSize = 52;
        this.currentSaveId = null;
        this.loopCount = 0;
        this.resetForNewBlind();
        for (var combination : Combination.values()) {
            this.handLevels.put(combination, 1);
        }
        this.wonPlanets.clear();
        this.activeJokers.clear();
        this.currentConstraint = BlindConstraint.NONE;
    }

    public boolean isInfiniteMode() { return infiniteMode; }
    public void setInfiniteMode(boolean infiniteMode) { this.infiniteMode = infiniteMode; }
    public int getLoopCount() { return loopCount; }
    public void setLoopCount(int loopCount) { this.loopCount = loopCount; }

    public List<JokerType> getActiveJokers() { return List.copyOf(this.activeJokers); }
    public boolean addJoker(JokerType joker) {
        Objects.requireNonNull(joker);
        if (this.activeJokers.contains(joker)) {
            return false;
        }
        if (this.activeJokers.size() < 5) {
            this.activeJokers.add(joker);
            return true;
        }
        return false;
    }

    public boolean removeJoker(JokerType joker) {
        return this.activeJokers.remove(joker);
    }

    public boolean isJokersFull() {
        return this.activeJokers.size() >= 5;
    }

    public String getCurrentSaveId() { return currentSaveId; }
    public void setCurrentSaveId(String saveId) { this.currentSaveId = saveId; }
    public void addScore(long points) { this.currentScore += points; }
    public int getLevel(Combination combination) { return this.handLevels.getOrDefault(Objects.requireNonNull(combination), 1); }
    public void upgradeHand(Combination combination) {
        var combo = Objects.requireNonNull(combination);
        this.handLevels.put(combo, this.getLevel(combo) + 1);
    }
    public void addWonPlanet(Planet planet) { Objects.requireNonNull(planet); this.wonPlanets.add(planet); }
    public boolean hasPlanet(Planet planet) { Objects.requireNonNull(planet); return this.wonPlanets.contains(planet); }

    public Set<Planet> getWonPlanets() { return Set.copyOf(this.wonPlanets); }

    public void loadState(long score, int hands, int discards, int deckSize, int blindIndex, boolean isInfinite, int loop) {
        this.currentScore = score;
        this.handsLeft = hands;
        this.discardsLeft = discards;
        this.deckSize = deckSize;
        this.currentBlindIndex = blindIndex;
        this.infiniteMode = isInfinite;
        this.loopCount = loop;
    }

    public void setLevel(Combination combo, int level) { this.handLevels.put(combo, level); }
    public BlindConstraint getCurrentConstraint() { return this.currentConstraint; }
    public void setCurrentConstraint(BlindConstraint constraint) { this.currentConstraint = constraint; }
    public int getCurrentBlindIndex() { return this.currentBlindIndex; }
    public long getCurrentScore() { return this.currentScore; }
    public int getHandsLeft() { return this.handsLeft; }
    public void useHand() { this.handsLeft--; }
    public int getDiscardsLeft() { return this.discardsLeft; }
    public void useDiscard() { this.discardsLeft--; }
    public int getDeckSize() { return this.deckSize; }
    public void setDeckSize(int deckSize) { this.deckSize = deckSize; }
}