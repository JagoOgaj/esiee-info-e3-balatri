package esiee.info.e3.model;

public interface IGameFeature {
    String getName();
    default long applyModifier(long currentScore, GameState state) { return currentScore; }
    default int applyMultiplierBonus(int currentMult) { return currentMult; }
}
