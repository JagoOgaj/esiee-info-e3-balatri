package esiee.info.e3.config.enums;

import java.awt.Color;

public enum OverlayType {
  INFO(Color.WHITE),
  ERROR(new Color(255, 80, 80)),
  VICTORY(new Color(255, 215, 0)),
  DEFEAT(new Color(255, 50, 50));

  private final Color color;

  OverlayType(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
