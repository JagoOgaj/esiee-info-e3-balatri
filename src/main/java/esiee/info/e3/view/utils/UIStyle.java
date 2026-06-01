package esiee.info.e3.view.utils;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;

public record UIStyle(
    Color backgroundColor,
    Color borderColor,
    int borderRadius,
    int padding,
    int margin,
    float borderWidth,
    Color textColor,
    Font font,
    Color shadowColor,
    int shadowOffset,
    int maxWidth,
    int maxHeight) {

  public UIStyle {
    Objects.requireNonNull(backgroundColor);
    Objects.requireNonNull(borderColor);
    Objects.requireNonNull(textColor);
    Objects.requireNonNull(shadowColor);

    if (borderRadius < 0) {
      throw new IllegalArgumentException();
    }
    if (padding < 0) {
      throw new IllegalArgumentException();
    }
    if (margin < 0) {
      throw new IllegalArgumentException();
    }
    if (borderWidth < 0) {
      throw new IllegalArgumentException();
    }
    if (maxWidth < 0) {
      throw new IllegalArgumentException();
    }
    if (maxHeight < 0) {
      throw new IllegalArgumentException();
    }
  }

  public static class Builder {
    private Color bg = new Color(0, 0, 0, 0);
    private Color border = new Color(0, 0, 0, 0);
    private int radius = 0;
    private int padding = 0;
    private int margin = 0;
    private float borderW = 0f;
    private Color text = Color.WHITE;
    private Font f = null;
    private Color shadowC = new Color(0, 0, 0, 0);
    private int shadowO = 0;
    private int maxW = 0;
    private int maxH = 0;

    public Builder bg(Color c) {
      this.bg = Objects.requireNonNull(c);
      return this;
    }

    public Builder border(Color c, float w) {
      this.border = Objects.requireNonNull(c);
      if (w < 0) {
        throw new IllegalArgumentException();
      }
      this.borderW = w;
      return this;
    }

    public Builder radius(int r) {
      if (r < 0) {
        throw new IllegalArgumentException();
      }
      this.radius = r;
      return this;
    }

    public Builder padding(int p) {
      if (p < 0) {
        throw new IllegalArgumentException();
      }
      this.padding = p;
      return this;
    }

    public Builder margin(int m) {
      if (m < 0) {
        throw new IllegalArgumentException();
      }
      this.margin = m;
      return this;
    }

    public Builder text(Color c) {
      this.text = Objects.requireNonNull(c);
      return this;
    }

    public Builder font(Font f) {
      this.f = f;
      return this;
    }

    public Builder shadow(Color c, int o) {
      this.shadowC = Objects.requireNonNull(c);
      this.shadowO = o;
      return this;
    }

    public Builder maxWidth(int w) {
      if (w < 0) {
        throw new IllegalArgumentException();
      }
      this.maxW = w;
      return this;
    }

    public Builder maxHeight(int h) {
      if (h < 0) {
        throw new IllegalArgumentException();
      }
      this.maxH = h;
      return this;
    }

    public UIStyle build() {
      return new UIStyle(
          bg, border, radius, padding, margin, borderW, text, f, shadowC, shadowO, maxW, maxH);
    }
  }
}
