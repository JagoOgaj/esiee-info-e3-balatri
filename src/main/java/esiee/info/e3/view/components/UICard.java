package esiee.info.e3.view.components;

import esiee.info.e3.domain.Card;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.UIComponent;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import java.util.function.Supplier;

public final class UICard implements UIComponent {
  private final Card card;
  private final boolean isFaceUp;
  private final Supplier<Boolean> isSelected;
  private final Runnable onClick;
  private final ViewMain context;
  private final double ratio;
  private boolean isHovered;

  public UICard(
      Card card,
      boolean isFaceUp,
      Supplier<Boolean> isSelected,
      Runnable onClick,
      ViewMain context,
      double ratio) {
    this.card = card;
    this.isFaceUp = isFaceUp;
    this.isSelected = Objects.requireNonNull(isSelected);
    this.onClick = onClick;
    this.context = Objects.requireNonNull(context);
    this.ratio = ratio;
  }

  private Rectangle getBounds(int x, int y, int width, int height) {
    var finalW = width;
    var finalH = (int) (width / this.ratio);

    if (finalH > height) {
      finalH = height;
      finalW = (int) (height * this.ratio);
    }

    var drawX = x + (width - finalW) / 2;
    var drawY = y + (height - finalH) / 2;

    return new Rectangle(drawX, drawY, finalW, finalH);
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    var bounds = this.getBounds(x, y, width, height);

    g.setColor(new Color(0, 0, 0, 150));
    g.fillRoundRect(bounds.x + 6, bounds.y + 6, bounds.width, bounds.height, 15, 15);

    g.setColor(Color.WHITE);
    g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);

    var img = this.context.getImage(this.getImagePath());

    if (img != null) {
      var oldClip = g.getClip();
      g.setClip(
          new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15));
      g.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);
      g.setClip(oldClip);
    } else {
      this.renderFallback(g, bounds);
    }

    if (this.isHovered && this.onClick != null) {
      g.setColor(new Color(255, 255, 255, 50));
      g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
    }

    if (this.isSelected.get()) {
      g.setColor(new Color(255, 215, 0, 220));
      g.setStroke(new BasicStroke(4));
      g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
    }
  }

  private void renderFallback(Graphics2D g, Rectangle bounds) {
    if (this.isFaceUp && this.card != null) {
      g.setColor(Color.BLACK);
      g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
      g.setFont(this.context.getGameFont().deriveFont(20f));
      g.drawString(this.card.rank().getLabel(), bounds.x + 10, bounds.y + 30);
      g.drawString(this.card.suit().name().substring(0, 3), bounds.x + 10, bounds.y + 60);
    } else {
      g.setColor(new Color(40, 80, 200));
      g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
    }
  }

  private String getImagePath() {
    if (!this.isFaceUp) {
      return "/cards/back.png";
    }
    if (this.card == null) {
      return "";
    }

    var suitPart = this.card.suit().name().toLowerCase();
    var rankPart =
        switch (this.card.rank().name().toLowerCase()) {
          case "rank_ace" -> "rank_as";
          case "rank_king" -> "rank_roi";
          default -> this.card.rank().name().toLowerCase();
        };

    return "/cards/" + suitPart + "_" + rankPart + ".png";
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) {
    var bounds = this.getBounds(x, y, width, height);
    if (bounds.contains(mx, my)) {
      if (this.onClick != null) {
        this.onClick.run();
      }
      return true;
    }
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int width, int height) {
    var bounds = this.getBounds(x, y, width, height);
    this.isHovered = bounds.contains(mx, my);
  }
}
