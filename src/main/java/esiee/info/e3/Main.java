package esiee.info.e3;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.config.IGameConfig;
import esiee.info.e3.config.enums.FontConstant;
import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.manager.ISaveManager;
import esiee.info.e3.manager.SaveManager;
import esiee.info.e3.model.gameModel.GameModel;
import esiee.info.e3.model.gameState.GameSateEnum;
import esiee.info.e3.view.main.ViewMain;
import esiee.info.e3.view.pages.*;
import java.awt.*;

public class Main {
  static void main() {
    IGameConfig gameConfig = new GameConfig();
    Font pixelFont = gameConfig.getPixelFont(FontConstant.FONT_BOLD_PIXEL.getPath(), 24f);
    ISaveManager saveManager = new SaveManager(gameConfig);
    GameModel model = new GameModel(gameConfig.getAllBlinds());
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    if (model.isGameActive()) {
                      saveManager.saveGame(model, GameSateEnum.PROGRESS);
                    }
                  } catch (Exception e) {
                    System.err.println(e.getMessage());
                  }
                }));

    ViewMain view = new ViewMain(pixelFont);
    model.addObserver(view);
    IGameController controller = new GameController(model, view, saveManager);

    view.addRoute(RoutesEnum.HOME, new HomePage(view, controller));
    view.addRoute(RoutesEnum.GAME, new GamePage(view, controller));
    view.addRoute(RoutesEnum.SHOP, new ShopPage(view, controller));
    view.addRoute(RoutesEnum.SAVES, new SavesPage(view, controller, saveManager));
    view.navigateTo(RoutesEnum.HOME, false);

    controller.init();
  }
}
