package esiee.info.e3.view.components;

import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.UIComponent;
import java.awt.*;
import java.util.Objects;

public final class UIAnimatedCoin implements UIComponent {
  private final ViewMain context;
  private int frameCount;

  public UIAnimatedCoin(ViewMain context) {
    this.context = Objects.requireNonNull(context);
    this.frameCount = 0;
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    this.frameCount++;
    var animationSpeed = 4; // Adjust speed as needed
    // Assuming 8 frames: Coin_01.png to Coin_08.png
    var currentFrame = (this.frameCount / animationSpeed) % 8 + 1;
    String formattedFrame = String.format("%02d", currentFrame);
    
    var path = "/money/Coin_" + formattedFrame + ".png";
    var img = this.context.getImage(path);

    if (img != null) {
        g.drawImage(img, x, y, width, height, null);
    }
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int w, int h) {
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int w, int h) {}
}