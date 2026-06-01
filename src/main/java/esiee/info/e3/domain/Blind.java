package esiee.info.e3.domain;

import java.util.Objects;

public record Blind(int id, String name, long score) {
  public Blind {
    Objects.requireNonNull(name);
    if (score < 0) {
      throw new IllegalArgumentException("score can't be negative");
    }
    if (id < 1 || id > 31) {
      throw new IllegalArgumentException("id should be between 1 and 31");
    }
  }
}
