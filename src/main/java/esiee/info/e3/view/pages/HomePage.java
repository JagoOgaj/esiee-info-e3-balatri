package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.UIButton;
import esiee.info.e3.view.components.UIContainer;
import esiee.info.e3.view.components.UIText;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class HomePage implements IPage {
  private final UIContainer root;

  public HomePage(ViewMain context) {
    var font = Objects.requireNonNull(context).getGameFont();
    var bgStyle = new UIStyle.Builder().bg(new Color(20, 40, 30)).build();
    var titleStyle = new UIStyle.Builder().text(Color.WHITE).font(font.deriveFont(80f)).build();

    var btnStyle =
        new UIStyle.Builder()
            .bg(new Color(40, 150, 40))
            .hoverBg(new Color(60, 200, 60))
            .text(Color.WHITE)
            .font(font.deriveFont(24f))
            .radius(20)
            .margin(50)
            .shadow(new Color(20, 60, 20), 5)
            .maxWidth(400)
            .maxHeight(100)
            .build();

    this.root = new UIContainer(4, 1, bgStyle);
    this.root.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_GAME_TITLE.getText(), titleStyle), 1, 0, 0.5, 1.0);
    this.root.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_PLAY.getText(), btnStyle, () -> context.navigateTo("game")),
        2,
        0,
        0.5,
        1.0);
  }

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    this.root.render(g, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {
    this.root.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void handlePointerMove(int mx, int my, float sw, float sh) {
    this.root.handlePointerMove(mx, my, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void update(
      GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {}

  @Override
  public void showOverlay(String message, Color color, Runnable onClose) {}
}
