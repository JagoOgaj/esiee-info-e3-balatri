package esiee.info.e3.view;

import com.github.forax.zen.Application;
import com.github.forax.zen.PointerEvent;
import com.github.forax.zen.ScreenInfo;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.interfaces.IView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;

public class ViewMain implements IView {
  private final Map<String, IPage> routes = new HashMap<>();
  private final Map<String, BufferedImage> imageCache = new HashMap<>();
  private final Font gameFont;
  private GameController controller;
  private IPage currentPage;
  private ScreenInfo lastScreenInfo;

  public ViewMain(Font gameFont) {
    this.gameFont = Objects.requireNonNull(gameFont);
  }

  public void addRoute(String name, IPage page) {
    this.routes.put(name, page);
  }

  public void navigateTo(String name) {
    var page = this.routes.get(name);
    if (page != null) {
      this.currentPage = page;
      this.controller.refreshView();
    } else {
      System.err.println("Route not found : " + name);
    }
  }

  @Override
  public void setController(GameController controller) {
    this.controller = Objects.requireNonNull(controller);
  }

  @Override
  public void update(
      GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {
    Objects.requireNonNull(this.currentPage).update(state, hand, selectedCards, eval);
  }

  @Override
  public void start() {
    Application.run(
        Color.BLACK,
        context -> {
          for (; ; ) {
            this.lastScreenInfo = context.getScreenInfo();
            context.renderFrame(
                graphics ->
                    render((Graphics2D) graphics, lastScreenInfo.width(), lastScreenInfo.height()));

            var event = context.pollOrWaitEvent(30);
            if (event == null) {
              continue;
            }

            if (event instanceof PointerEvent pointer) {
              var mx = pointer.location().x();
              var my = pointer.location().y();

              if (pointer.action() == PointerEvent.Action.POINTER_DOWN) {
                this.handlePointerClick(mx, my);
              } else if (pointer.action() == PointerEvent.Action.POINTER_MOVE) {
                Objects.requireNonNull(this.lastScreenInfo);
                Objects.requireNonNull(this.currentPage);
                currentPage.handlePointerMove(
                    mx, my, lastScreenInfo.width(), lastScreenInfo.height());
              }
            }
          }
        });
  }

  private void render(Graphics2D g, float sw, float sh) {
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    g.setColor(new Color(20, 40, 30));
    g.fillRect(0, 0, (int) sw, (int) sh);
    g.setFont(this.gameFont);
    Objects.requireNonNull(this.currentPage).render(g, sw, sh);
  }

  private void handlePointerClick(int mx, int my) {
    Objects.requireNonNull(this.currentPage);
    Objects.requireNonNull(this.lastScreenInfo);
    this.currentPage.handlePointerClick(
        mx, my, this.lastScreenInfo.width(), this.lastScreenInfo.height());
  }

  public BufferedImage getImage(String path) {
    return this.imageCache.computeIfAbsent(
        path,
        p -> {
          try (InputStream is = getClass().getResourceAsStream(p)) {
            return (is != null) ? ImageIO.read(is) : null;
          } catch (IOException e) {
            return null;
          }
        });
  }

  public Font getGameFont() {
    return this.gameFont;
  }

  @Override
  public void showMessage(String message) {
    Objects.requireNonNull(this.currentPage).showOverlay(message, Color.WHITE, null);
  }

  @Override
  public void showError(String error) {
    Objects.requireNonNull(this.currentPage).showOverlay(error, new Color(255, 80, 80), null);
  }

  @Override
  public void showGameOver(boolean victory, long finalScore) {
    var msg =
        victory
            ? TextConstant.TEXT_CONSTANT_VICTORY.getText() + finalScore
            : TextConstant.TEXT_CONSTANT_DEFEAT.getText() + finalScore;
    var color = victory ? new Color(255, 215, 0) : new Color(255, 50, 50);

    Objects.requireNonNull(this.currentPage)
        .showOverlay(
            msg,
            color,
            () -> {
              controller.resetGame();
              navigateTo("home");
            });
  }
}
