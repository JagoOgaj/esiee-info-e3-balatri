package esiee.info.e3;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.config.enums.FontConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.manager.SaveManager;
import esiee.info.e3.model.*;
import esiee.info.e3.view.*;
import esiee.info.e3.view.interfaces.IView;
import esiee.info.e3.view.pages.*;
import java.awt.*;

public class Main {
  public static void main(String[] args) {
    GameModel model = new GameModel(new DeckManager(), new HandEvaluator(), new ScoreCalculator());

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
              GameState state = model.getState();
              boolean isGameActive = state.getCurrentScore() > 0 ||
                      state.getHandsLeft() < 4 ||
                      state.getDiscardsLeft() < 3 ||
                      state.getCurrentBlindIndex() > 0;

              if (isGameActive) {
                  SaveManager.saveGame(model, "EN_COURS");
              }
          } catch (Exception e) {
              System.err.println(e.getMessage());
          }
      }));
    if (args.length > 0 && args[0].equals("-console")) {
      IView view = new ViewConsole();
      GameController controller = new GameController(model, view);
      view.setController(controller);
      controller.init();
    } else {
      Font pixelFont = GameConfig.loadPixelFont(FontConstant.FONT_BOLD_PIXEL.getPath(), 24f);
      ViewMain view = new ViewMain(pixelFont);
      GameController controller = new GameController(model, view);

      view.setController(controller);
      view.addRoute("home", new HomePage(view, controller));
      view.addRoute("game", new GamePage(view, controller));
      view.addRoute("saves", new SavesPage(view, controller));
      view.navigateTo("home");

      controller.init();
    }
  }
}
