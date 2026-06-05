package esiee.info.e3.domain;

import java.util.Objects;

public record SaveSlot(
    String id,
    String date,
    String lastBlindName,
    int blindId,
    long score,
    String status,
    boolean canResume,
    boolean infiniteMode,
    int loopCount) {

  public SaveSlot {
    Objects.requireNonNull(id);
    Objects.requireNonNull(date);
    Objects.requireNonNull(lastBlindName);
    if (blindId < 0) {
      throw new IllegalArgumentException();
    }
    if (score < 0) {
      throw new IllegalArgumentException();
    }
    if (loopCount < 0) {
      throw new IllegalArgumentException();
    }
  }
}
