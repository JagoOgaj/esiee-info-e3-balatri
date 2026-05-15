package esiee.info.e3.view.components;

import esiee.info.e3.view.interfaces.UIComponent;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

public record UIText(
    Supplier<String> textSupplier, UIStyle style, Supplier<Color> dynamicColorSupplier)
    implements UIComponent {

  public UIText {
    Objects.requireNonNull(textSupplier);
    Objects.requireNonNull(style);
  }

  public UIText(Supplier<String> textSupplier, UIStyle style) {
    this(textSupplier, style, null);
  }

  public UIText(String text, UIStyle style) {
    this(() -> text, style, null);
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    var margin = this.style.margin();
    var drawX = x + margin;
    var drawY = y + margin;
    var drawW = width - (margin * 2);
    var drawH = height - (margin * 2);

    if (this.style.backgroundColor().getAlpha() > 0) {
      g.setColor(this.style.backgroundColor());
      g.fillRoundRect(
          drawX, drawY, drawW, drawH, this.style.borderRadius(), this.style.borderRadius());
    }

    if (this.style.font() != null) {
      g.setFont(this.style.font());
    }

    var textColor =
        (this.dynamicColorSupplier != null && this.dynamicColorSupplier.get() != null)
            ? this.dynamicColorSupplier.get()
            : this.style.textColor();

    g.setColor(textColor);

    var currentText = Objects.requireNonNullElse(this.textSupplier.get(), "");

    var metrics = g.getFontMetrics();
    var textWidth = metrics.stringWidth(currentText);
    var textHeight = metrics.getAscent();

    g.drawString(
        currentText, drawX + (drawW - textWidth) / 2, drawY + (drawH + textHeight) / 2 - 4);
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int w, int h) {
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int w, int h) {}
}
