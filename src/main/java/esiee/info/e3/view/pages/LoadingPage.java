package esiee.info.e3.view.pages;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.IPage;
import java.awt.*;
import java.util.List;

public class LoadingPage implements IPage {
    private final ViewMain context;
    private final String targetRoute;
    private final long startTime;

    public LoadingPage(ViewMain context, String targetRoute) {
        this.context = context;
        this.targetRoute = targetRoute;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void render(Graphics2D g, float sw, float sh) {
        long elapsed = System.currentTimeMillis() - startTime;

        g.setColor(new Color(15, 25, 20));
        g.fillRect(0, 0, (int) sw, (int) sh);

        long cycle = elapsed % 800;
        float offsetX = 0;
        boolean leftCardInFront = true;

        if (cycle < 150) {
            offsetX = (cycle / 150f) * 110f;
        } else if (cycle < 300) {
            offsetX = 110f - (((cycle - 150) / 150f) * 110f);
            leftCardInFront = false;
        } else {
            offsetX = 0;
            leftCardInFront = false;
        }

        var backImg = context.getImage("/cards/back.png");
        int cardW = 120;
        int cardH = 168;
        int centerX = (int) (sw / 2 - (float) cardW / 2);
        int centerY = (int) (sh / 2 - (float) cardH / 2 - 40);

        if (leftCardInFront) {
            drawCard(g, backImg, centerX + (int)offsetX, centerY, cardW, cardH);
            drawCard(g, backImg, centerX - (int)offsetX, centerY, cardW, cardH);
        } else {
            drawCard(g, backImg, centerX - (int)offsetX, centerY, cardW, cardH);
            drawCard(g, backImg, centerX + (int)offsetX, centerY, cardW, cardH);
        }

        int dotsCount = (int) ((elapsed / 400) % 4);
        String text = "Chargement " + ".".repeat(dotsCount);

        g.setFont(context.getGameFont().deriveFont(30f));
        g.setColor(Color.WHITE);
        var metrics = g.getFontMetrics();
        int textX = (int) (sw / 2 - (float) metrics.stringWidth("Chargement ...") / 2);
        int textY = centerY + cardH + 70;

        g.drawString(text, textX, textY);

        if (elapsed > 2200) {
            context.navigateTo(targetRoute, false);
        }
    }

    private void drawCard(Graphics2D g, Image img, int x, int y, int w, int h) {
        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
        } else {
            g.setColor(new Color(40, 80, 200));
            g.fillRoundRect(x, y, w, h, 10, 10);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, w, h, 10, 10);
        }
    }

    @Override public void handlePointerClick(int mx, int my, float sw, float sh) {}
    @Override public void handlePointerMove(int mx, int my, float sw, float sh) {}
    @Override public void update(GameState s, List<Card> h, List<Card> sc, EvaluatedHand e) {}
    @Override public void showOverlay(String m, Color c, Runnable o) {}
}