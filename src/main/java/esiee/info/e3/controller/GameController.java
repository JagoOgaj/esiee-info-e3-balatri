package esiee.info.e3.controller;

import esiee.info.e3.config.enums.OverlayType;
import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.manager.ISaveManager;
import esiee.info.e3.model.*;
import esiee.info.e3.view.IView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public record GameController(IGameModel model, IView view, ISaveManager saveManager)
    implements IGameController {

  public GameController {
    Objects.requireNonNull(model);
    Objects.requireNonNull(view);
    Objects.requireNonNull(saveManager);
  }

  @Override
  public void startGame(boolean infiniteMode) {
    model.executeStartGameAction(infiniteMode);
    view().navigateTo(RoutesEnum.GAME, true);
  }

  @Override
  public void goTo(RoutesEnum route, boolean withLoading) {
      view.navigateTo(route, withLoading);
  }

  @Override
  public void init() {
    model.notifyObservers();
    view.start();
  }

  @Override
  public void resetGame() {
    this.model.resetGame();
    view.navigateTo(RoutesEnum.HOME, false);
  }

  @Override
  public void loadGameFromJson(String saveId) {
    Objects.requireNonNull(saveId);
    model.resetSelectedCards();

    switch (model) {
      case GameModel concreteModel -> saveManager.loadGame(saveId, concreteModel);
      default -> {}
    }

    model.notifyObservers();
    view.navigateTo(RoutesEnum.GAME, true);
  }

  @Override
  public void toggleCardSelection(Card card) {
    Objects.requireNonNull(card);
    var isOK = model.toggleCardSelection(card);
    if (!isOK) {
      view.showOverlay(
          OverlayType.ERROR, TextConstant.TEXT_CONSTANT_ERROR_MAX_CARDS.getText(), null);
    }
  }

  @Override
  public void handlePlay() {
    var result = model.executePlayAction();

    switch (result) {
      case TurnResultError err -> view.showOverlay(OverlayType.ERROR, err.message(), null);
      case TurnResultHandPlayed hp -> {
        executeConditionalSave(GameSateEnum.PROGRESS);
        var message =
            TextConstant.TEXT_CONSTANT_SCORE.getText()
                + TextConstant.TEXT_CONSTANT_HAND_PLAYED.getText()
                + hp.scoreGained()
                + TextConstant.TEXT_CONSTANT_POINTS.getText();
        view.showOverlay(OverlayType.INFO, message, null);
      }
      case TurnResultBlindBeaten bb -> {
        executeConditionalSave(GameSateEnum.PROGRESS);
        view.showOverlay(OverlayType.INFO, bb.message(), null);
      }
      case TurnResultGameWon _ -> {
        executeConditionalSave(GameSateEnum.VICTORY);
        view.showOverlay(
            OverlayType.VICTORY,
            TextConstant.TEXT_CONSTANT_VICTORY.getText() + model.getState().getCurrentScore(),
            this::resetGame);
      }
      case TurnResultGameLost _ -> {
        saveHighScoreIfBetter(model.getState());
        executeConditionalSave(GameSateEnum.DEFEAT);
        view.showOverlay(
            OverlayType.DEFEAT,
            TextConstant.TEXT_CONSTANT_DEFEAT.getText() + model.getState().getCurrentScore(),
            this::resetGame);
      }
      case TurnResultFailure f -> f.exception().printStackTrace();
    }
  }

  @Override
  public void handleDiscard() {
    if (model.isEmptySelectedCards()) {
      view.showOverlay(
          OverlayType.ERROR, TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText(), null);
      return;
    }
    try {
      model.executeDiscardAction();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getJokerRarity(JokerType joker) {
    Objects.requireNonNull(joker);
    return model.getJokerRarityLabel(joker);
  }

  @Override
  public long getExpectedScore() {
    return model.calculateExpectedScore();
  }

  @Override
  public void saveAndQuit() {
    executeConditionalSave(GameSateEnum.PROGRESS);
    view.navigateTo(RoutesEnum.HOME, false);
  }

  @Override
  public void exitGame() {
    System.exit(0);
  }

  @Override
  public long getHighScore() {
    try {
      var f = new File(saveManager.getPathFileSave());
      if (f.exists()) {
        return Long.parseLong(Files.readString(f.toPath()).trim());
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return 0;
  }

  private void saveHighScoreIfBetter(IGameState state) {
    Objects.requireNonNull(state);
    try {
      long currentHigh = 0;
      File f = new File(saveManager.getPathFileSave());
      if (f.exists()) currentHigh = Long.parseLong(Files.readString(f.toPath()).trim());

      if (state.getCurrentScore() > currentHigh) {
        Files.writeString(f.toPath(), String.valueOf(state.getCurrentScore()));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  private void executeConditionalSave(GameSateEnum status) {
    switch (model) {
      case GameModel concreteModel -> saveManager.saveGame(concreteModel, status);
      default -> {}
    }
  }
}
