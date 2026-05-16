package esiee.info.e3;

import esiee.info.e3.config.enums.FontConstant;
import esiee.info.e3.config.GameConfig;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.model.*;
import esiee.info.e3.view.*;
import esiee.info.e3.view.interfaces.IView;
import esiee.info.e3.view.pages.*;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel(new StandardDeckManager(), new StandardHandEvaluator(), new StandardScoreCalculator());

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
            view.addRoute("home", new HomePage(view));
            view.addRoute("game", new GamePage(view, controller));
            view.navigateTo("home");

            controller.init();
        }
    }
}