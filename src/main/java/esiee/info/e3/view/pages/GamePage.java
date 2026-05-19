package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.*;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GamePage implements IPage {
  private final ViewMain context;
  private final GameController controller;
  private final double CARD_RATIO;
  private GameState currentState;
  private List<Card> currentHand;
  private List<Card> selectedCards;
  private EvaluatedHand currentEvaluation;
  private UIContainer rootContainer;
  private UIContainer handContainer;
  private UIContainer playedCardsContainer;
  private String overlayMessage;
  private Color overlayColor;
  private Runnable onOverlayClose;

  public GamePage(ViewMain context, GameController controller) {
    this.context = Objects.requireNonNull(context);
    this.controller = Objects.requireNonNull(controller);
    this.currentHand = List.of();
    this.selectedCards = List.of();
    this.overlayColor = Color.white;
    this.CARD_RATIO = 71.0 / 95.0;
    this.buildLayout();
  }

  private void buildLayout() {
    var rootStyle = new UIStyle.Builder().bg(new Color(30, 70, 40)).build();
    this.rootContainer = new UIContainer(100, 100, rootStyle);

    this.rootContainer.addComponent(this.buildSidebar(), 0, 0, 1.0, 0.25);
    this.rootContainer.addComponent(this.buildGameplayArea(), 0, 25, 1.0, 0.75);
  }

  private UIContainer buildGameplayArea() {
    var transStyle = new UIStyle.Builder().build();
    var gameplayArea = new UIContainer(100, 100, transStyle);
    this.setupPlayedCardsZone(gameplayArea);
    var bottomBox = new UIContainer(100, 100, transStyle);
    this.setupHandZone(bottomBox);
    this.setupDeckZone(bottomBox);

    gameplayArea.addComponent(bottomBox, 66, 0, 0.34, 1.0);
    return gameplayArea;
  }

  private UIContainer buildSidebar() {
    var sidebarStyle =
        new UIStyle.Builder()
            .bg(new Color(40, 35, 30))
            .border(new Color(100, 80, 50), 3f)
            .radius(20)
            .padding(10)
            .build();
    var sidebar = new UIContainer(100, 100, sidebarStyle);

    this.addBlindHeader(sidebar);
    this.addGoalSection(sidebar);
    this.addComboSection(sidebar);
    this.addCurrentScoreSection(sidebar);
    this.addStatsSection(sidebar);
    this.addActionButtons(sidebar);

    return sidebar;
  }

  private void addBlindHeader(UIContainer sidebar) {
    var style =
        new UIStyle.Builder()
            .bg(new Color(220, 50, 50))
            .radius(10)
            .text(Color.WHITE)
            .font(this.context.getGameFont().deriveFont(32f))
            .build();
    sidebar.addComponent(new UIText(this::getBlindName, style), 2, 5, 0.08, 0.90);
  }

  private void addGoalSection(UIContainer sidebar) {
    var box = this.createStandardBox();
    var smallLabel = this.createLabelStyle(22f);
    var scoreStyle = this.createValueStyle(38f);

    box.addComponent(new UIAnimatedImage(this::getBlindId, this.context), 5, 5, 0.55, 0.90);
    box.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_SCORE_TO_ACHIEVE.getText(), smallLabel),
        62,
        0,
        0.15,
        1.0);
    box.addComponent(
        new UIText(() -> String.valueOf(this.getBlindScore()), scoreStyle, this::getScoreColor),
        78,
        0,
        0.20,
        1.0);

    sidebar.addComponent(box, 12, 5, 0.18, 0.90);
  }

  private void addComboSection(UIContainer sidebar) {
    var box = this.createStandardBox();
    var smallLabel = this.createLabelStyle(22f);
    var valStyle = this.createValueStyle(28f);

    box.addComponent(
        new UIImage(this::getCurrentPlanetPath, this.context, this.CARD_RATIO), 5, 5, 0.45, 0.90);

    box.addComponent(new UIText(this::getComboLevelText, smallLabel), 50, 0, 0.12, 1.0);

    box.addComponent(new UIText(this::getComboLabel, smallLabel), 62, 0, 0.15, 1.0);
    box.addComponent(new UIText(this::getComboStats, valStyle), 78, 0, 0.20, 1.0);

    sidebar.addComponent(box, 32, 5, 0.18, 0.90);
  }

  private void addCurrentScoreSection(UIContainer sidebar) {
    var style = new UIStyle.Builder().bg(Color.BLACK).border(Color.ORANGE, 2f).radius(10).build();
    var box = new UIContainer(100, 100, style);

    box.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_ACTUAL_SCORE.getText(), this.createLabelStyle(22f)),
        10,
        0,
        0.40,
        1.0);
    box.addComponent(
        new UIText(() -> String.valueOf(this.getCurrentScoreValue()), this.createValueStyle(28f)),
        50,
        0,
        0.40,
        1.0);

    sidebar.addComponent(box, 52, 5, 0.08, 0.90);
  }

  private void addStatsSection(UIContainer sidebar) {
    var blue = new UIStyle.Builder().bg(new Color(40, 80, 200)).radius(10).build();
    var red = new UIStyle.Builder().bg(new Color(200, 50, 50)).radius(10).build();

    var mBox = new UIContainer(100, 100, blue);
    mBox.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_HAND.getText(), this.createLabelStyle(20f)),
        15,
        0,
        0.35,
        1.0);
    mBox.addComponent(
        new UIText(() -> String.valueOf(this.getHandsLeftValue()), this.createValueStyle(26f)),
        45,
        0,
        0.45,
        1.0);

    var dBox = new UIContainer(100, 100, red);
    dBox.addComponent(
        new UIText(TextConstant.TEXT_CONSTANT_DEFAUSSE.getText(), this.createLabelStyle(20f)),
        15,
        0,
        0.35,
        1.0);
    dBox.addComponent(
        new UIText(() -> String.valueOf(this.getDiscardsLeftValue()), this.createValueStyle(26f)),
        45,
        0,
        0.45,
        1.0);

    sidebar.addComponent(mBox, 62, 5, 0.07, 0.42);
    sidebar.addComponent(dBox, 62, 53, 0.07, 0.42);
  }

  private void addActionButtons(UIContainer sidebar) {
    var discStyle =
        new UIStyle.Builder()
            .bg(new Color(180, 40, 40))
            .radius(10)
            .text(Color.WHITE)
            .font(this.context.getGameFont().deriveFont(30f))
            .build();
    var playStyle =
        new UIStyle.Builder()
            .bg(new Color(255, 160, 0))
            .radius(15)
            .text(Color.WHITE)
            .font(this.context.getGameFont().deriveFont(32f))
            .build();

    sidebar.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_DEFAUSSED.getText(),
            discStyle,
            this.controller::handleDiscard),
        72,
        5,
        0.08,
        0.90);

      sidebar.addComponent(
        new UIButton(
            TextConstant.TEXT_CONSTANT_PLAY_HAND.getText(), playStyle,() -> this.controller.handlePlay(Optional.empty())),
        82,
        5,
        0.14,
        0.90);
  }

  private void setupPlayedCardsZone(UIContainer area) {
    var zoneStyle = new UIStyle.Builder().bg(new Color(0, 0, 0, 100)).radius(15).build();
    this.playedCardsContainer = new UIContainer(100, 100, zoneStyle);
    area.addComponent(this.playedCardsContainer, 33, 10, 0.30, 0.80);
  }

  private void setupHandZone(UIContainer bottom) {
    var zoneStyle = new UIStyle.Builder().bg(new Color(0, 0, 0, 100)).radius(15).build();
    var counterStyle =
        new UIStyle.Builder()
            .text(Color.WHITE)
            .font(this.context.getGameFont().deriveFont(22f))
            .build();

    this.handContainer = new UIContainer(100, 100, zoneStyle);
    bottom.addComponent(this.handContainer, 10, 5, 0.75, 0.70);
    bottom.addComponent(
        new UIText(
            () -> this.getHandSize() + TextConstant.TEXT_CONSTANT_BY.formatBy("8"), counterStyle),
        86,
        5,
        0.14,
        0.15);
  }

  private void setupDeckZone(UIContainer bottom) {
    var counterStyle =
        new UIStyle.Builder()
            .text(Color.WHITE)
            .font(this.context.getGameFont().deriveFont(22f))
            .build();
    var deckCard = new UICard(null, false, () -> false, null, this.context, this.CARD_RATIO);

    bottom.addComponent(deckCard, 10, 80, 0.65, 0.15);
    bottom.addComponent(
        new UIText(
            () -> this.getDeckRemainingCards() + TextConstant.TEXT_CONSTANT_BY.formatBy("52"),
            counterStyle),
        78,
        80,
        0.15,
        0.15);
  }

  @Override
  public void update(
      GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {
    this.currentState = Objects.requireNonNull(state);
    this.currentHand = Objects.requireNonNull(hand);
    this.selectedCards = Objects.requireNonNull(selectedCards);
    this.currentEvaluation = eval;

    this.refreshContainers();
  }

  private void refreshContainers() {
    Objects.requireNonNull(this.handContainer).clearChildren();
    var handX = 2;
    for (var card : this.currentHand) {
      if (!this.selectedCards.contains(card)) {
        var uiCard =
            new UICard(
                card,
                true,
                () -> false,
                () -> this.controller.toggleCardSelection(card),
                this.context,
                this.CARD_RATIO);
        this.handContainer.addComponent(uiCard, 10, handX, 0.80, 0.11);
        handX += 12;
      }
    }
    this.updateCenterContainer();
  }

  private void updateCenterContainer() {
    Objects.requireNonNull(this.playedCardsContainer).clearChildren();

    if (this.overlayMessage != null) {
      this.renderOverlayMessage();
      return;
    }

    var playedX = 15;
    for (var card : this.currentHand) {
      if (this.selectedCards.contains(card)) {
        var uiCard =
            new UICard(
                card,
                true,
                () -> true,
                () -> this.controller.toggleCardSelection(card),
                this.context,
                this.CARD_RATIO);
        this.playedCardsContainer.addComponent(uiCard, 10, playedX, 0.80, 0.12);
        playedX += 14;
      }
    }
  }

  private void renderOverlayMessage() {
    var style =
        new UIStyle.Builder()
            .text(this.overlayColor)
            .font(this.context.getGameFont().deriveFont(45f))
            .shadow(Color.BLACK, 5)
            .build();
    this.playedCardsContainer.addComponent(
        new UIText(this.overlayMessage, style), 10, 10, 0.80, 0.80);
  }

  private UIContainer createStandardBox() {
    var style =
        new UIStyle.Builder()
            .bg(new Color(30, 30, 30))
            .shadow(new Color(0, 0, 0, 150), 5)
            .radius(12)
            .build();
    return new UIContainer(100, 100, style);
  }

  private UIStyle createLabelStyle(float size) {
    return new UIStyle.Builder()
        .text(Color.WHITE)
        .font(this.context.getGameFont().deriveFont(size))
        .build();
  }

  private UIStyle createValueStyle(float size) {
    return new UIStyle.Builder()
        .text(Color.WHITE)
        .font(this.context.getGameFont().deriveFont(size))
        .shadow(new Color(0, 0, 0, 150), 3)
        .build();
  }

  private int getHandSize() {
    return Objects.requireNonNull(this.currentHand).size()
        - Objects.requireNonNull(this.selectedCards).size();
  }

  private String getComboLabel() {
    return this.currentEvaluation != null
        ? this.currentEvaluation.combo().getLabel()
        : TextConstant.TEXT_CONSTANT_SELECT.getText();
  }

  private String getComboStats() {
    return this.currentEvaluation != null
        ? this.currentEvaluation.chips() + " x " + this.currentEvaluation.multiplier()
        : TextConstant.TEXT_CONSTANT_DEFAULT_0X0.getText();
  }

  private String getComboLevelText() {
    if (this.currentEvaluation == null || this.currentState == null) {
      return "Lvl 1";
    }
    return "Lvl " + this.currentState.getLevel(this.currentEvaluation.combo());
  }

  private long getCurrentScoreValue() {
    return Objects.requireNonNull(this.currentState).getCurrentScore();
  }

  private int getHandsLeftValue() {
    return Objects.requireNonNull(this.currentState).getHandsLeft();
  }

  private int getDiscardsLeftValue() {
    return Objects.requireNonNull(this.currentState).getDiscardsLeft();
  }

  private int getBlindId() {
    return Objects.requireNonNull(this.currentState).getCurrentBlind().id();
  }

  private String getBlindName() {
    return Objects.requireNonNull(this.currentState).getCurrentBlind().name();
  }

  private int getBlindScore() {
    return Objects.requireNonNull(this.currentState).getCurrentBlind().score();
  }

  private int getDeckRemainingCards() {
    return Objects.requireNonNull(this.currentState).getDeckSize();
  }

  private String getCurrentPlanetPath() {
    if (this.currentEvaluation == null) return null;
    var planet = Planet.fromCombination(this.currentEvaluation.combo());
    return planet != null ? "/planets/" + planet.getFileName() : null;
  }

  private Color getScoreColor() {
    var score = this.getBlindScore();
    if (score >= 10000) {
      return new Color(255, 50, 50);
    }
    if (score >= 5000) {
      return new Color(255, 150, 0);
    }
    if (score >= 1000) {
      return new Color(255, 215, 0);
    }
    return Color.WHITE;
  }

  @Override
  public void showOverlay(String m, Color c, Runnable o) {
    this.overlayMessage = m;
    this.overlayColor = c;
    this.onOverlayClose = o;
    this.updateCenterContainer();
  }

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    this.rootContainer.render(g, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {
    if (this.overlayMessage != null) {
      this.overlayMessage = null;

      if (this.onOverlayClose != null) {
        var action = this.onOverlayClose;
        this.onOverlayClose = null;
        action.run();
      } else {
        this.updateCenterContainer();
      }
      return;
    }

    this.rootContainer.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void handlePointerMove(int mx, int my, float sw, float sh) {
    if (this.overlayMessage == null) {
      this.rootContainer.handlePointerMove(mx, my, 0, 0, (int) sw, (int) sh);
    }
  }
}
