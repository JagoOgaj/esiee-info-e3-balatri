package esiee.info.e3.view.main;

import esiee.info.e3.config.enums.OverlayType;
import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.domain.GameSnapshot;
import java.awt.*;
import java.awt.image.BufferedImage;

public sealed interface IView permits ViewMain {
  void setController(IGameController controller);

  void update(GameSnapshot gameSnapshot);

  void start();

  Font getGameFont();

  BufferedImage getImage(String path);

  void onModelUpdated(GameSnapshot snapshot);

  void navigateTo(RoutesEnum route, boolean withLoading);

  void showOverlay(OverlayType type, String message, Runnable onClose);
}
