package esiee.info.e3.model;

import esiee.info.e3.domain.Combination;

import java.util.EnumMap;
import java.util.Map;

public class GameState {
    private long currentScore = 0;
    private int handsLeft = 4;
    private int discardsLeft = 3;
    private final Map<Combination, Integer> handLevels = new EnumMap<>(Combination.class);

    public GameState() {
        for (Combination c : Combination.values()) {
            handLevels.put(c, 1); // Niveau 1 par défaut
        }
    }

    public void addScore(long points) { this.currentScore += points; }
    public int getLevel(Combination c) { return handLevels.get(c); }
    public void upgradeHand(Combination c) { handLevels.put(c, handLevels.get(c) + 1); }
    public long getCurrentScore() { return currentScore; }
    public int getHandsLeft() { return handsLeft; }
    public void useHand() { handsLeft--; }
    public int getDiscardsLeft() { return discardsLeft; }
    public void useDiscard() { discardsLeft--; }
}
