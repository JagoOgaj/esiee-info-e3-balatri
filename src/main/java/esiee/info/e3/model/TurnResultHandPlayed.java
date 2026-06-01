package esiee.info.e3.model;

public record TurnResultHandPlayed(long scoreGained) implements TurnResult {
  public TurnResultHandPlayed {
    if (scoreGained < 0) {
      throw new IllegalArgumentException();
    }
  }
}
