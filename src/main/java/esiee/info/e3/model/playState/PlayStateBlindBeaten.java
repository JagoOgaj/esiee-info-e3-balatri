package esiee.info.e3.model.playState;

import java.util.Objects;

public record PlayStateBlindBeaten(int totalMoney, String message) implements PlayState {
  public PlayStateBlindBeaten {
    Objects.requireNonNull(message);
    if (totalMoney < 0) {
      throw new IllegalArgumentException();
    }
  }
}
