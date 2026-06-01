package esiee.info.e3.model;

import java.util.Objects;

public record TurnResultFailure(Throwable exception) implements TurnResult {
    public TurnResultFailure {
        Objects.requireNonNull(exception);
    }
}
