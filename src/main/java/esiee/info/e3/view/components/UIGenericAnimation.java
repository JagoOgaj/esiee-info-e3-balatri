package esiee.info.e3.view.components;

import esiee.info.e3.view.ViewMain;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class UIGenericAnimation implements UIComponent {
  private final ViewMain context;
  private final int animationSpeed;
  private final int maxFrames;
  private final Function<Integer, String> frameToPathBuilder;
  private final boolean preserveAspectRatio;
  private int frameCount;

  private UIGenericAnimation(
      ViewMain context,
      int animationSpeed,
      int maxFrames,
      Function<Integer, String> frameToPathBuilder,
      boolean preserveAspectRatio) {
    this.context = Objects.requireNonNull(context);
    this.animationSpeed = animationSpeed;
    this.maxFrames = maxFrames;
    this.frameToPathBuilder = Objects.requireNonNull(frameToPathBuilder);
    this.preserveAspectRatio = preserveAspectRatio;
    this.frameCount = 0;
  }

  public static UIGenericAnimation createCoinAnimation(ViewMain context) {
    return new UIGenericAnimation(
        context, 4, 8, frame -> String.format("/money/Coin_%02d.png", frame), false);
  }

  public static UIGenericAnimation createBlindChipAnimation(
      ViewMain context, Supplier<Integer> blindIdSupplier) {
    Objects.requireNonNull(blindIdSupplier);
    return new UIGenericAnimation(
        context,
        2,
        21,
        frame -> {
          int blindId = Objects.requireNonNullElse(blindIdSupplier.get(), 1);
          if (blindId < 1 || blindId > 31) blindId = 1;
          return "/sliced_chips/blind_" + blindId + "/chip_frame_" + frame + ".png";
        },
        true);
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    this.frameCount++;
    var currentFrame = (this.frameCount / this.animationSpeed) % this.maxFrames + 1;

    String path = this.frameToPathBuilder.apply(currentFrame);
    Image img = this.context.getImage(path);

    if (img != null) {
      if (this.preserveAspectRatio) {
        double ratio = 1.0;
        var finalW = width;
        var finalH = (int) (width / ratio);

        if (finalH > height) {
          finalH = height;
          finalW = (int) (height * ratio);
        }

        var drawX = x + (width - finalW) / 2;
        var drawY = y + (height - finalH) / 2;
        g.drawImage(img, drawX, drawY, finalW, finalH, null);
      } else {
        g.drawImage(img, x, y, width, height, null);
      }
    }
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int w, int h) {
    return false;
  }
}
