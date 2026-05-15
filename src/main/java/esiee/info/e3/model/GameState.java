package esiee.info.e3.model;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.enums.Combination;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameState {
    private final Map<Combination, Integer> handLevels;
    private final List<Blind> blinds;
    private long currentScore;
    private int handsLeft;
    private int discardsLeft;
    private int deckSize;
    private int currentBlindIndex;

    public GameState() {
        this.handLevels = new EnumMap<>(Combination.class);
        this.blinds = GameConfig.allBlinds();
        this.resetGame();
    }

    public Blind getCurrentBlind() {
        return this.blinds.get(this.currentBlindIndex);
    }

    public boolean nextBlind() {
        if (this.currentBlindIndex < this.blinds.size() - 1) {
            this.currentBlindIndex++;
            return true;
        }
        return false;
    }

    public void resetForNewBlind() {
        this.currentScore = 0;
        this.handsLeft = 4;
        this.discardsLeft = 3;
    }

    public final void resetGame() {
        this.currentBlindIndex = 0;
        this.deckSize = 52;
        this.resetForNewBlind();
        for (var combination : Combination.values()) {
            this.handLevels.put(combination, 1);
        }
    }

    public void addScore(long points) {
        this.currentScore += points;
    }

    public int getLevel(Combination combination) {
        return this.handLevels.getOrDefault(Objects.requireNonNull(combination), 1);
    }

    public void upgradeHand(Combination combination) {
        var combo = Objects.requireNonNull(combination);
        this.handLevels.put(combo, this.getLevel(combo) + 1);
    }

    public long getCurrentScore() {
        return this.currentScore;
    }

    public int getHandsLeft() {
        return this.handsLeft;
    }

    public void useHand() {
        this.handsLeft--;
    }

    public int getDiscardsLeft() {
        return this.discardsLeft;
    }

    public void useDiscard() {
        this.discardsLeft--;
    }

    public int getDeckSize() {
        return this.deckSize;
    }

    public void setDeckSize(int size) {
        this.deckSize = size;
    }
}