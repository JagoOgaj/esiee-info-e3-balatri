package esiee.info.e3.view.utils;

import esiee.info.e3.view.interfaces.UIComponent;

import java.util.Objects;

public record GridChild(
        UIComponent component, int row, int col, double weightH, double weightW) {
    public GridChild {
        Objects.requireNonNull(component);
    }
}
