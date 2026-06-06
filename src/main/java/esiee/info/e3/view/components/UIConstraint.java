package esiee.info.e3.view.components;

import esiee.info.e3.view.main.ViewMain;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

public record UIConstraint(
    ViewMain context,
    Supplier<Boolean> visibilitySupplier,
    Supplier<String> titleSupplier,
    Supplier<String> descriptionSupplier)
    implements UIComponent {
  public UIConstraint {
    Objects.requireNonNull(context);
    Objects.requireNonNull(visibilitySupplier);
    Objects.requireNonNull(titleSupplier);
    Objects.requireNonNull(descriptionSupplier);
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    if (!this.visibilitySupplier.get()) {
      return;
    }

    g.setColor(new Color(60, 20, 40));
    g.fillRoundRect(x, y, width, height, 8, 8);

    g.setColor(Color.MAGENTA);
    g.setStroke(new BasicStroke(2f));
    g.drawRoundRect(x, y, width, height, 8, 8);

    g.setColor(Color.WHITE);
    g.setFont(this.context.getGameFont().deriveFont(22f));
    String title = this.titleSupplier.get();
    if (title != null) {
      int titleX = x + (width - g.getFontMetrics().stringWidth(title)) / 2;
      int titleY = y + height / 2 - 5;
      g.drawString(title, titleX, titleY);
    }

    g.setColor(Color.ORANGE);
    g.setFont(this.context.getGameFont().deriveFont(16f));
    String desc = this.descriptionSupplier.get();
    if (desc != null) {
      int descX = x + (width - g.getFontMetrics().stringWidth(desc)) / 2;
      int descY = y + height / 2 + 18;
      g.drawString(desc, descX, descY);
    }
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) {
    return false;
  }
}
