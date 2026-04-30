package esiee.info.e3.domain;

import java.util.Objects;

public record Blind(String name, int score) {
    public Blind {
        Objects.requireNonNull(name);
        if (score < 0) {
            throw new IllegalArgumentException("score can't be negative");
        }
    }

    @Override
    public String toString() {
        return String.format("Name : %s, Score : %d\n", this.name, this.score);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return o instanceof Blind(String name1, int score1) && this.score == score1 && Objects.equals(name, name1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }
}
