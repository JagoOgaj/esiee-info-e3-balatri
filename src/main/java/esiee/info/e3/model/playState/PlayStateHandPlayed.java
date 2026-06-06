package esiee.info.e3.model.playState;

public record PlayStateHandPlayed(long scoreGained) implements PlayState {
  public PlayStateHandPlayed {
    if (scoreGained < 0) {
      throw new IllegalArgumentException();
    }
  }
}
