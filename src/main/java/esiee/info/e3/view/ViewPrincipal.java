package esiee.info.e3.view;

import com.github.forax.zen.Application;
import com.github.forax.zen.PointerEvent;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.model.GameState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPrincipal implements IView {
    private GameController controller;
    private GameState currentState;
    private List<Card> currentHand = List.of();
    private List<Card> selectedCards = List.of();

    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private String message = "Bienvenue dans Balatri !";

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void update(GameState state, List<Card> hand, List<Card> selectedCards) {
        this.currentState = state;
        this.currentHand = hand;
        this.selectedCards = selectedCards;
    }

    @Override
    public void showMessage(String message) { this.message = message; }
    @Override
    public void showError(String error) { this.message = "ERREUR : " + error; }
    @Override
    public void showGameOver(long finalScore) { this.message = "GAME OVER ! Score : " + finalScore; }

    private BufferedImage getImage(String path) {
        if (imageCache.containsKey(path)) return imageCache.get(path);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                imageCache.put(path, img);
                return img;
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger : " + path);
        }
        imageCache.put(path, null);
        return null;
    }

    private BufferedImage getCardSprite(Card card) {
        var suit = card.suit().name().replace("SUIT_", "").toLowerCase();
        var rank = card.rank().getLabel().toLowerCase();
        return getImage("/cards/suit_" + suit + "_rank_" + rank + ".png");
    }

    private BufferedImage getPlanetSprite(String planetName) {
        return getImage("/planets/" + planetName.toLowerCase() + ".png");
    }

    @Override
    public void start() {
        Application.run(Color.BLACK, context -> {
            for (;;) {
                context.renderFrame(graphics -> render((Graphics2D) graphics, context.getScreenInfo().width(), context.getScreenInfo().height()));

                var event = context.pollOrWaitEvent(30);
                if (event == null) continue;

                if (event instanceof PointerEvent pointer && pointer.action() == PointerEvent.Action.POINTER_DOWN) {
                    handlePointerClick((int)pointer.location().x(), (int)pointer.location().y());
                }
            }
        });
    }

    private void render(Graphics2D g, float sw, float sh) {
        g.setColor(new Color(20, 40, 30));
        g.fillRect(0, 0, (int) sw, (int) sh);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(message, 50, 50);

        if (currentState != null) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.drawString("Score: " + currentState.getCurrentScore(), 50, 90);
            g.drawString("Mains: " + currentState.getHandsLeft(), 50, 115);
            g.drawString("Défausses: " + currentState.getDiscardsLeft(), 50, 140);
        }

        int x = 50;
        int y = 300;
        for (Card card : currentHand) {
            var currentY = selectedCards.contains(card) ? y - 30 : y;
            drawCard(g, card, x, currentY);
            x += 90;
        }

        drawButton(g, 50, 500, 150, 50, "JOUER", new Color(40, 150, 40));
        drawButton(g, 220, 500, 150, 50, "DÉFAUSSER", new Color(150, 40, 40));
    }

    private void drawCard(Graphics2D g, Card card, int x, int y) {
        var w = 75;
        var h = 100;

        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 10, 10);

        BufferedImage img = getCardSprite(card);
        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
        } else {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(card.rank().getLabel(), x + 5, y + 20);
            String suitName = card.suit().name();
            String suitDisplay = suitName.length() > 5 ? suitName.substring(5, Math.min(7, suitName.length())) : suitName;
            g.drawString(suitDisplay, x + 5, y + 40);
        }

        if (selectedCards.contains(card)) {
            g.setColor(new Color(255, 255, 0, 100));
            g.setStroke(new BasicStroke(4));
            g.drawRoundRect(x, y, w, h, 10, 10);
        }
    }

    private void drawButton(Graphics2D g, int x, int y, int w, int h, String label, Color color) {
        g.setColor(color);
        g.fillRoundRect(x, y, w, h, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, w, h, 15, 15);
        g.drawString(label, x + 35, y + 32);
    }

    private void handlePointerClick(int mx, int my) {
        var x = 50;
        var y = 300;
        for (Card card : currentHand) {
            int currentY = selectedCards.contains(card) ? y - 30 : y;
            if (mx >= x && mx <= x + 75 && my >= currentY && my <= currentY + 100) {
                controller.toggleCardSelection(card);
                return;
            }
            x += 90;
        }

        if (my >= 500 && my <= 550) {
            if (mx >= 50 && mx <= 200) controller.handlePlay();
            if (mx >= 220 && mx <= 370) controller.handleDiscard();
        }
    }
}