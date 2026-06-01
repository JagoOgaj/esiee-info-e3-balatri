package esiee.info.e3.view.components;

import java.awt.*;

public sealed interface UIComponent
    permits UIButton,
        UICard,
        UIConstraint,
        UIContainer,
        UIGenericAnimation,
        UIImage,
        UIJoker,
        UIText {
  void render(Graphics2D g, int x, int y, int width, int height);

  boolean handlePointerClick(int mx, int my, int x, int y, int width, int height);
}
