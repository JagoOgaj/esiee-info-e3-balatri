package esiee.info.e3.view.main;

import com.github.forax.zen.Application;
import com.github.forax.zen.PointerEvent;
import com.github.forax.zen.ScreenInfo;
import esiee.info.e3.config.enums.OverlayType;
import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.model.gameState.IGameState;
import esiee.info.e3.view.pages.IPage;
import esiee.info.e3.view.pages.LoadingPage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;

public final class ViewMain implements IView {
  private final Map<RoutesEnum, IPage> routes = new HashMap<>();
  private final Map<String, BufferedImage> imageCache = new HashMap<>();
  private final Font gameFont;
  private IPage currentPage;
  private ScreenInfo lastScreenInfo;
  private IGameState cachedState;
  private List<Card> cachedHand;
  private List<Card> cachedSelectedCards;
  private EvaluatedHand cachedEval;

  public ViewMain(Font gameFont) {
    this.gameFont = Objects.requireNonNull(gameFont);
  }

  public void addRoute(RoutesEnum route, IPage page) {
    this.routes.put(Objects.requireNonNull(route), Objects.requireNonNull(page));
  }

  @Override
  public void navigateTo(RoutesEnum route, boolean withLoading) {
    Objects.requireNonNull(route);
    if (withLoading) {
      this.currentPage = new LoadingPage(this, route);
    } else {
      var page = this.routes.get(route);
      if (page != null) {
        this.currentPage = page;
        if (this.cachedState != null) {
          this.currentPage.update(
              new GameSnapshot(
                  this.cachedState, this.cachedHand, this.cachedSelectedCards, this.cachedEval));
        }
      } else {
        System.err.println("Not found " + route.name());
      }
    }
  }

  @Override
  public void onModelUpdated(GameSnapshot gameSnapshot) {
    Objects.requireNonNull(gameSnapshot);
    this.cachedState = gameSnapshot.state();
    this.cachedHand = gameSnapshot.hand();
    this.cachedSelectedCards = gameSnapshot.selectedCards();
    this.cachedEval = gameSnapshot.evaluation();

    this.update(gameSnapshot);
  }

  @Override
  public void setController(IGameController controller) {
    // this.controller = Objects.requireNonNull(controller);
  }

  @Override
  public void update(GameSnapshot gameSnapshot) {
    Objects.requireNonNull(gameSnapshot);
    if (this.currentPage != null) {
      this.currentPage.update(gameSnapshot);
    }
  }

  @Override
  public void start() {
    Application.run(
        Color.BLACK,
        context -> {
          for (; ; ) {
            this.lastScreenInfo = context.getScreenInfo();
            context.renderFrame(
                graphics -> render(graphics, lastScreenInfo.width(), lastScreenInfo.height()));

            var event = context.pollOrWaitEvent(16);
            if (event == null) continue;

            if (event instanceof PointerEvent pointer) {
              var mx = pointer.location().x();
              var my = pointer.location().y();

              if (pointer.action() == PointerEvent.Action.POINTER_DOWN) {
                this.handlePointerClick(mx, my);
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
    if (this.currentPage != null) {
      this.currentPage.render(g, sw, sh);
    }
  }

  private void handlePointerClick(int mx, int my) {
    if (this.currentPage != null && this.lastScreenInfo != null) {
      this.currentPage.handlePointerClick(
          mx, my, this.lastScreenInfo.width(), this.lastScreenInfo.height());
    }
  }

  @Override
  public BufferedImage getImage(String path) {
    Objects.requireNonNull(path);
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

  @Override
  public Font getGameFont() {
    return this.gameFont;
  }

  @Override
  public void showOverlay(OverlayType type, String message, Runnable onClose) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(message);
    Objects.requireNonNull(this.currentPage).showOverlay(message, type.getColor(), onClose);
  }
}
