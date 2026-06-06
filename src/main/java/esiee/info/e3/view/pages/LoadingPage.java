package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.view.main.IView;
import java.awt.*;
import java.util.Objects;

public final class LoadingPage implements IPage {
  private final IView context;
  private final RoutesEnum targetRoute;
  private final long startTime;

  public LoadingPage(IView context, RoutesEnum targetRoute) {
    this.context = Objects.requireNonNull(context);
    this.targetRoute = Objects.requireNonNull(targetRoute);
    this.startTime = System.currentTimeMillis();
  }

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    var elapsed = System.currentTimeMillis() - startTime;

    g.setColor(new Color(15, 25, 20));
    g.fillRect(0, 0, (int) sw, (int) sh);

    var cycle = elapsed % 800;
    float offsetX;
    boolean leftCardInFront = true;

    if (cycle < 150) {
      offsetX = (cycle / 150f) * 110f;
    } else if (cycle < 300) {
      offsetX = 110f - (((cycle - 150) / 150f) * 110f);
      leftCardInFront = false;
    } else {
      offsetX = 0;
      leftCardInFront = false;
    }

    var backImg = context.getImage("/cards/back.png");
    var cardW = 120;
    var cardH = 168;
    var centerX = (int) (sw / 2 - (float) cardW / 2);
    var centerY = (int) (sh / 2 - (float) cardH / 2 - 40);

    if (leftCardInFront) {
      drawCard(g, backImg, centerX + (int) offsetX, centerY, cardW, cardH);
      drawCard(g, backImg, centerX - (int) offsetX, centerY, cardW, cardH);
    } else {
      drawCard(g, backImg, centerX - (int) offsetX, centerY, cardW, cardH);
      drawCard(g, backImg, centerX + (int) offsetX, centerY, cardW, cardH);
    }

    int dotsCount = (int) ((elapsed / 400) % 4);
    String text = TextConstant.TEXT_CONSTANT_LOADING.getText() + " " + ".".repeat(dotsCount);

    g.setFont(context.getGameFont().deriveFont(30f));
    g.setColor(Color.WHITE);
    var metrics = g.getFontMetrics();
    var textX =
        (int)
            (sw / 2
                - (float)
                        metrics.stringWidth(
                            TextConstant.TEXT_CONSTANT_LOADING.getText() + " " + "...")
                    / 2);
    var textY = centerY + cardH + 70;

    g.drawString(text, textX, textY);

    if (elapsed > 2200) {
      context.navigateTo(targetRoute, false);
    }
  }

  private void drawCard(Graphics2D g, Image img, int x, int y, int w, int h) {
    if (img != null) {
      g.drawImage(img, x, y, w, h, null);
    } else {
      g.setColor(new Color(40, 80, 200));
      g.fillRoundRect(x, y, w, h, 10, 10);
      g.setColor(Color.WHITE);
      g.drawRoundRect(x, y, w, h, 10, 10);
    }
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {}

  @Override
  public void update(GameSnapshot gameSnapshot) {}

  @Override
  public void showOverlay(String m, Color c, Runnable o) {}
}
