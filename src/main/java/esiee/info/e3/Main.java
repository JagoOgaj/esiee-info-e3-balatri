package esiee.info.e3;

import esiee.info.e3.config.enums.FontConstant;
import esiee.info.e3.config.GameConfig;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.model.*;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.view.*;
import esiee.info.e3.view.pages.*;

import java.awt.*;

public class Main {
    static void main() {
        var pixelFont = GameConfig.loadPixelFont(FontConstant.FONT_BOLD_PIXEL.getPath(), 24f);
        var model = new GameModel(new StandardDeckManager(), new StandardHandEvaluator(), new StandardScoreCalculator());
        var view = new ViewMain(pixelFont);
        var controller = new GameController(model, view);
        view.setController(controller);
        view.addRoute("home", new HomePage(view));
        view.addRoute("game", new GamePage(view, controller));
        view.navigateTo("home");
        controller.init();
    }
}