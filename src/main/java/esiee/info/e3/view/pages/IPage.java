package esiee.info.e3.view.pages;

import esiee.info.e3.domain.GameSnapshot;
import java.awt.*;

public sealed interface IPage permits GamePage, HomePage, LoadingPage, SavesPage, ShopPage {
  void render(Graphics2D g, float sw, float sh);

  void handlePointerClick(int mx, int my, float sw, float sh);

  void update(GameSnapshot gameSnapshot);

  void showOverlay(String message, Color color, Runnable onClose);
}
