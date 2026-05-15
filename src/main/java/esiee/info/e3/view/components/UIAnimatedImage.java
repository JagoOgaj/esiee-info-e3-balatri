package esiee.info.e3.view.components;

import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.UIComponent;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

public final class UIAnimatedImage implements UIComponent {
  private final Supplier<Integer> blindIdSupplier;
  private final ViewMain context;
  private int frameCount;

  public UIAnimatedImage(Supplier<Integer> blindIdSupplier, ViewMain context) {
    this.blindIdSupplier = Objects.requireNonNull(blindIdSupplier);
    this.context = Objects.requireNonNull(context);
    this.frameCount = 0;
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    this.frameCount++;
    var animationSpeed = 2;
    var currentFrame = (this.frameCount / animationSpeed) % 21 + 1;
    int blindId = Objects.requireNonNullElse(this.blindIdSupplier.get(), 1);

    if (blindId < 1 || blindId > 31) {
      blindId = 1;
    }

    var path = "/sliced_chips/blind_" + blindId + "/chip_frame_" + currentFrame + ".png";
    var img = this.context.getImage(path);

    var ratio = 1.0;
    var finalW = width;
    var finalH = (int) (width / ratio);

    if (finalH > height) {
      finalH = height;
      finalW = (int) (height * ratio);
    }

    var drawX = x + (width - finalW) / 2;
    var drawY = y + (height - finalH) / 2;

    if (img != null) {
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
