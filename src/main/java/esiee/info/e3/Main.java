package esiee.info.e3;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.config.IGameConfig;
import esiee.info.e3.config.enums.FontConstant;
import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.manager.ISaveManager;
import esiee.info.e3.manager.SaveManager;
import esiee.info.e3.model.*;
import esiee.info.e3.view.*;

import esiee.info.e3.view.pages.*;
import java.awt.*;

public class Main {
  public static void main() {
    IGameConfig gameConfig = new GameConfig();
    Font pixelFont = gameConfig.getPixelFont(FontConstant.FONT_BOLD_PIXEL.getPath(), 24f);
    ISaveManager saveManager = new SaveManager(gameConfig);
    GameModel model = new GameModel(gameConfig.getAllBlinds());
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    var state = model.getState();
                    boolean isGameActive =
                        state.getCurrentScore() > 0
                            || state.getHandsLeft() < 4
                            || state.getDiscardsLeft() < 3
                            || state.getCurrentBlindIndex() > 0;

                    if (isGameActive) {
                        saveManager.saveGame(model, GameSateEnum.PROGRESS);
                    }
                  } catch (Exception e) {
                    System.err.println(e.getMessage());
                  }
                }));

    ViewMain view = new ViewMain(pixelFont);
    model.addObserver(view);
    IGameController controller = new GameController(model, view, saveManager);

    view.setController(controller);
    view.addRoute(RoutesEnum.HOME, new HomePage(view, controller));
    view.addRoute(RoutesEnum.GAME, new GamePage(view, controller));
    view.addRoute(RoutesEnum.SHOP, new ShopPage(view));
    view.addRoute(RoutesEnum.SAVES, new SavesPage(view, controller, saveManager));
    view.navigateTo(RoutesEnum.HOME);

    controller.init();
  }
}
