package esiee.info.e3.view.interfaces;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.GameState;
import java.awt.*;
import java.util.List;

public interface IPage {
  void render(Graphics2D g, float sw, float sh);

  void handlePointerClick(int mx, int my, float sw, float sh);

  void handlePointerMove(int mx, int my, float sw, float sh);

  void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval);

  void showOverlay(String message, Color color, Runnable onClose);
}
