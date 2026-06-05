package esiee.info.e3.model.gameState;

import java.util.Objects;

public enum GameSateEnum {
  PROGRESS("EN_COURS"),
  VICTORY("VICTOIRE"),
  DEFEAT("DÉFAITE");

  private final String label;

  GameSateEnum(String label) {
    this.label = Objects.requireNonNull(label);
  }

  public String getLabel() {
    return this.label;
  }
}
