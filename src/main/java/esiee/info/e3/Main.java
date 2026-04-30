package esiee.info.e3;

import esiee.info.e3.controller.GameController;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.model.StandardDeckManager;
import esiee.info.e3.model.StandardHandEvaluator;
import esiee.info.e3.model.StandardScoreCalculator;
import esiee.info.e3.view.ViewPrincipal;

public class Main {
    static void main() {
        ViewPrincipal view = new ViewPrincipal();

        GameModel model = new GameModel(
                new StandardDeckManager(),
                new StandardHandEvaluator(),
                new StandardScoreCalculator()
        );

        GameController controller = new GameController(model, view);
        view.setController(controller);
        controller.init();
    }
}
