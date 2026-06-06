package esiee.info.e3.model.playState;

import java.util.Objects;

public record PlayStateError(String message) implements PlayState {
  public PlayStateError {
    Objects.requireNonNull(message);
  }
}
