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

  @Override
  public String toString() {
    return String.format("Id : %d, Name : %s, Score : %d\n", this.id, this.name, this.score);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    return o instanceof Blind(int id1, String name1, long score1)
        && this.id == id1
        && this.score == score1
        && Objects.equals(name, name1);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, score);
  }
}
