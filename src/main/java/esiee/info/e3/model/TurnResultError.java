package esiee.info.e3.model;

import java.util.Objects;

public record TurnResultError(String message) implements TurnResult {
    public TurnResultError {
        Objects.requireNonNull(message);
    }
}
