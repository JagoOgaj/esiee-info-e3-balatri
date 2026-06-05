package esiee.info.e3.model.playState;

import java.util.Objects;

public record PlayStateFailure(Throwable exception) implements PlayState {
  public PlayStateFailure {
    Objects.requireNonNull(exception);
  }
}
