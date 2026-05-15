package esiee.info.e3.view.components;

import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.UIComponent;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

public record UIImage(Supplier<String> pathSupplier, ViewMain context, double ratio)
    implements UIComponent {

  public UIImage {
    Objects.requireNonNull(pathSupplier);
    Objects.requireNonNull(context);
    if (ratio <= 0) {
      throw new IllegalArgumentException("ratio should be positive");
    }
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    var path = this.pathSupplier.get();
    var finalW = width;
    var finalH = (int) (width / this.ratio);

    if (finalH > height) {
      finalH = height;
      finalW = (int) (height * this.ratio);
    }

    var drawX = x + (width - finalW) / 2;
    var drawY = y + (height - finalH) / 2;

    if (path == null || path.isEmpty()) {
      g.setColor(new Color(50, 50, 50, 150));
      g.fillRoundRect(drawX, drawY, finalW, finalH, 10, 10);
      g.setColor(new Color(150, 150, 150));
      g.setStroke(
          new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5}, 0));
      g.drawRoundRect(drawX, drawY, finalW, finalH, 10, 10);
      return;
    }

    var img = this.context.getImage(path);
    if (img != null) {
      g.setColor(new Color(0, 0, 0, 100));
      g.fillRoundRect(drawX + 4, drawY + 4, finalW, finalH, 10, 10);
      g.drawImage(img, drawX, drawY, finalW, finalH, null);
    }
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int w, int h) {
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int w, int h) {}
}
