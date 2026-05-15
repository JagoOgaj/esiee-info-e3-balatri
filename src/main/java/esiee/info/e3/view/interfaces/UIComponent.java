package esiee.info.e3.view.interfaces;

import java.awt.*;

public interface UIComponent {
  void render(Graphics2D g, int x, int y, int width, int height);

  boolean handlePointerClick(int mx, int my, int x, int y, int width, int height);

  void handlePointerMove(int mx, int my, int x, int y, int width, int height);
}
