package esiee.info.e3.view.utils;


import esiee.info.e3.view.components.UICard;

import java.util.Objects;

public record BackgroundCard(
        float x,
        float y,
        float speed,
        float rotation,
        float rotSpeed,
        UICard uiComponent
) {
    public BackgroundCard {
        Objects.requireNonNull(uiComponent);

        if (speed < 0) {
            throw new IllegalArgumentException();
        }
        if (rotSpeed == 0) {
            throw new IllegalArgumentException();
        }
    }
}