package esiee.info.e3.model;

import java.util.Objects;

public record TurnResultBlindBeaten(int totalMoney, String message) implements TurnResult {
  public TurnResultBlindBeaten {
    Objects.requireNonNull(message);
    if (totalMoney < 0) {
      throw new IllegalArgumentException();
    }
  }
}
