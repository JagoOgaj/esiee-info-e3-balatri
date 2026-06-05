package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.view.IView;
import esiee.info.e3.view.components.UIButton;
import esiee.info.e3.view.components.UICard;
import esiee.info.e3.view.components.UIContainer;
import esiee.info.e3.view.components.UIText;
import esiee.info.e3.view.utils.BackgroundCard;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class HomePage implements IPage {
  private final UIContainer root;
  private final List<BackgroundCard> backgroundCards;
  private final Random random;
  private final IView context;
  private boolean initialized;

  public HomePage(IView context, IGameController controller) {
    this.context = Objects.requireNonNull(context);
    IGameController controller1 = Objects.requireNonNull(controller);
    this.backgroundCards = new ArrayList<>();
    this.random = new Random();
    this.initialized = false;
    var font = context.getGameFont();
    var rootStyle = new UIStyle.Builder().bg(new Color(0, 0, 0, 0)).build();
    var classicBtnStyle =
        new UIStyle.Builder()
            .bg(new Color(34, 112, 63))
            .text(Color.WHITE)
            .font(font.deriveFont(28f))
            .radius(5)
            .border(Color.WHITE, 3f)
            .margin(10)
            .shadow(new Color(0, 0, 0, 100), 8)
            .maxWidth(450)
            .maxHeight(70)
            .build();

    var infiniteBtnStyle =
        new UIStyle.Builder()
            .bg(new Color(148, 0, 211))
            .text(Color.WHITE)
            .font(font.deriveFont(28f))
            .radius(5)
            .border(Color.WHITE, 3f)
            .margin(10)
            .shadow(new Color(0, 0, 0, 100), 8)
            .maxWidth(450)
            .maxHeight(70)
            .build();

    var btnStyle =
        new UIStyle.Builder()
            .bg(new Color(40, 150, 40))
            .text(Color.WHITE)
            .font(font.deriveFont(28f))
            .radius(5)
            .border(Color.WHITE, 3f)
            .margin(10)
            .shadow(new Color(0, 0, 0, 100), 8)
            .maxWidth(450)
            .maxHeight(70)
            .build();

    var quitBtnStyle =
        new UIStyle.Builder()
            .bg(new Color(180, 40, 40))
            .text(Color.WHITE)
            .font(font.deriveFont(28f))
            .radius(5)
            .border(Color.WHITE, 3f)
            .margin(10)
            .shadow(new Color(0, 0, 0, 100), 8)
            .maxWidth(450)
            .maxHeight(70)
            .build();

    var titleStyle =
        new UIStyle.Builder()
            .text(new Color(150, 150, 150))
            .font(font.deriveFont(160f))
            .shadow(new Color(0, 0, 0, 240), 8)
            .build();

    var versionStyle =
        new UIStyle.Builder().text(new Color(150, 150, 150)).font(font.deriveFont(12f)).build();

    this.root = new UIContainer(10, 1, rootStyle);

    this.root.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_GAME_TITLE.getText(), titleStyle), 1, 0, 0.4, 1.0);

    this.root.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_CLASSIC_MOD.getText(),
            classicBtnStyle,
            () -> controller.startGame(false)),
        4,
        0,
        0.12,
        1.0);

    this.root.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_INFINITY_MOD.getText(),
            infiniteBtnStyle,
            () -> controller.startGame(true)),
        5,
        0,
        0.12,
        1.0);

    this.root.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_PARTIES_LISTING.getText(), btnStyle, () -> controller.goTo(RoutesEnum.SAVES, true)),
        6,
        0,
        0.12,
        1.0);

    this.root.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_QUIT_GAME.getText(), quitBtnStyle, controller1::exitGame),
        7,
        0,
        0.12,
        1.0);

    this.root.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_VERSION_GAME.getText(), versionStyle),
        8,
        0,
        0.1,
        1.0);
  }

  private void initBackgroundCards(float sw, float sh) {
    for (var i = 0; i < 15; i++) {
      createRandomCard(sw, sh, true);
    }
    initialized = true;
  }

  private void createRandomCard(float sw, float sh, boolean randomY) {
    var r = Rank.values()[random.nextInt(Rank.values().length)];
    var s = Suit.values()[random.nextInt(Suit.values().length)];
    var card = new Card(r, s);

    var x = random.nextFloat() * sw;
    var y = randomY ? random.nextFloat() * sh : sh + 200;
    var speed = 1.0f + random.nextFloat() * 2.5f;
    var rot = random.nextFloat() * 360;
    var rotS = -1f + random.nextFloat() * 2f;

    var ui = new UICard(card, true, () -> false, null, context, 71.0 / 95.0);
    backgroundCards.add(new BackgroundCard(x, y, speed, rot, rotS, ui));
  }

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    if (!initialized) initBackgroundCards(sw, sh);

    g.setColor(new Color(15, 25, 20));
    g.fillRect(0, 0, (int) sw, (int) sh);

    for (var i = backgroundCards.size() - 1; i >= 0; i--) {
      BackgroundCard bc = backgroundCards.get(i);

      var newY = bc.y() - bc.speed();
      var newRot = bc.rotation() + bc.rotSpeed();

      if (newY < -200) {
        backgroundCards.remove(i);
        createRandomCard(sw, sh, false);
        continue;
      }

      var updatedCard =
          new BackgroundCard(bc.x(), newY, bc.speed(), newRot, bc.rotSpeed(), bc.uiComponent());
      backgroundCards.set(i, updatedCard);

      var oldTransform = g.getTransform();
      g.translate(updatedCard.x(), updatedCard.y());
      g.rotate(Math.toRadians(updatedCard.rotation()));

      updatedCard.uiComponent().render(g, -50, -70, 100, 140);

      g.setTransform(oldTransform);
    }

    this.root.render(g, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {
    this.root.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void update(GameSnapshot gameSnapshot) {}

  @Override
  public void showOverlay(String message, Color color, Runnable onClose) {}
}
