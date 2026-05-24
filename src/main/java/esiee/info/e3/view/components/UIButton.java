package esiee.info.e3.view.components;

import esiee.info.e3.view.interfaces.UIComponent;
import esiee.info.e3.view.utils.Bounds;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.Objects;

public class UIButton implements UIComponent {
  private final String text;
  private final Runnable action;
  private final UIStyle style;
  private boolean isHovered;

  public UIButton(String text, UIStyle style, Runnable action) {
    this.text = Objects.requireNonNull(text);
    this.style = Objects.requireNonNull(style);
    this.action = Objects.requireNonNull(action);
    this.isHovered = false;
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    var b = this.calculateBounds(x, y, width, height);

    if (this.style.shadowOffset() > 0) {
      g.setColor(this.style.shadowColor());
      g.fillRoundRect(
          b.x() + this.style.shadowOffset(),
          b.y() + this.style.shadowOffset(),
          b.w(),
          b.h(),
          this.style.borderRadius(),
          this.style.borderRadius());
    }

    var bgColor =
        (this.isHovered && this.style.hoverBackgroundColor() != null)
            ? this.style.hoverBackgroundColor()
            : this.style.backgroundColor();

    g.setColor(bgColor);
    g.fillRoundRect(
        b.x(), b.y(), b.w(), b.h(), this.style.borderRadius(), this.style.borderRadius());

    if (this.style.borderWidth() > 0f) {
      g.setColor(this.style.borderColor());
      g.setStroke(new BasicStroke(this.style.borderWidth()));
      g.drawRoundRect(
          b.x(), b.y(), b.w(), b.h(), this.style.borderRadius(), this.style.borderRadius());
    }

    g.setColor(this.style.textColor());
    if (this.style.font() != null) {
      g.setFont(this.style.font());
    }

    var metrics = g.getFontMetrics();
    g.drawString(
        this.text,
        b.x() + (b.w() - metrics.stringWidth(this.text)) / 2,
        b.y() + (b.h() + metrics.getAscent()) / 2 - 4);
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) {
    var b = this.calculateBounds(x, y, width, height);
    if (mx >= b.x() && mx <= b.x() + b.w() && my >= b.y() && my <= b.y() + b.h()) {
      this.action.run();
      return true;
    }
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int width, int height) {
    var b = this.calculateBounds(x, y, width, height);
    this.isHovered = (mx >= b.x() && mx <= b.x() + b.w() && my >= b.y() && my <= b.y() + b.h());
  }

  private Bounds calculateBounds(int x, int y, int width, int height) {
    var margin = this.style.margin();
    var allottedW = width - (margin * 2);
    var allottedH = height - (margin * 2);
    var finalW =
        (this.style.maxWidth() > 0) ? Math.min(allottedW, this.style.maxWidth()) : allottedW;
    var finalH =
        (this.style.maxHeight() > 0) ? Math.min(allottedH, this.style.maxHeight()) : allottedH;
    var drawX = x + margin + (allottedW - finalW) / 2;
    var drawY = y + margin + (allottedH - finalH) / 2;
    return new Bounds(drawX, drawY, finalW, finalH);
  }

}
